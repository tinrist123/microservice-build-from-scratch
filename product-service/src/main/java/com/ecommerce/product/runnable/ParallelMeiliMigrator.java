package com.ecommerce.product.runnable;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
public class ParallelMeiliMigrator {

    // ====== TUNING KNOBS ======
    private final int batchSize = 20000; // docs per batch
    private final int consumerCount = 6; // parallel consumer virtual threads
    private final int queueCapacity = 20; // number of batches buffered
    private final int maxInFlightBatches = 8; // backpressure to Meili

    // ====== DEPENDENCIES ======
    private final JdbcTemplate jdbc;
    private final RestClient meiliClient;
    private final ExecutorService virtualExecutor;

    // ====== CHECKPOINT ======
    private final String checkpointName = "products_created_id";

    private final String meiliIndexName;

    public ParallelMeiliMigrator(
            JdbcTemplate jdbc,
            RestClient meiliSearchRestClient,
            @Qualifier("contextAwareVirtualExecutor") ExecutorService virtualExecutor) {
        this.jdbc = jdbc;
        this.meiliClient = meiliSearchRestClient;
        this.virtualExecutor = virtualExecutor;
        this.meiliIndexName = "products";
    }

    // --------- PUBLIC API ----------
    public void migrateOnceFast() throws InterruptedException, ExecutionException {
        ensureCheckpointTable();

        Checkpoint cp = loadCheckpoint(checkpointName);
        System.out.printf("Start migration from checkpoint createdAt=%s id=%s%n",
                cp.lastCreatedAt, cp.lastId);

        BlockingQueue<List<Map<String, Object>>> queue = new ArrayBlockingQueue<>(queueCapacity);
        AtomicBoolean producerDone = new AtomicBoolean(false);
        AtomicReference<Exception> producerError = new AtomicReference<>();
        Semaphore inFlight = new Semaphore(maxInFlightBatches);

        // Producer reads DB sequentially using (created_at, id) keyset
        Future<?> producerFuture = virtualExecutor.submit(() -> {
            try {
                Checkpoint local = cp;
                int batchTime = 1;
                while (true) {
                    List<Map<String, Object>> batch = fetchBatch(local, batchSize);
                    if (batch.isEmpty())
                        break;

                    // Put batch into queue (blocks if queue is full)
                    queue.put(batch);

                    // Advance local checkpoint based on last record in the batch
                    Map<String, Object> last = batch.getLast();
                    local = new Checkpoint(
                            (LocalDateTime) last.get("createdAt"),
                            (String) last.get("id"));

                    log.info("enrich data and product with {} time with {} - {}", batchTime, last.get("createdAt"),
                            last.get("id"));
                    batchTime++;
                }
            } catch (Exception e) {
                producerError.set(e);
                throw new RuntimeException("Producer failed", e);
            } finally {
                producerDone.set(true);
            }
        });

        // Consumers write to Meili concurrently on virtual threads
        List<Future<?>> consumerFutures = new ArrayList<>();
        Runnable consumerTask = () -> {
            try {
                while (true) {
                    List<Map<String, Object>> batch = queue.poll(200, TimeUnit.MILLISECONDS);
                    if (batch == null) {
                        if (producerDone.get() && queue.isEmpty())
                            return;
                        continue;
                    }

                    inFlight.acquire();
                    try {
                        upsertToMeili(batch);

                        // Save checkpoint after successful write
                        Map<String, Object> last = batch.getLast();
                        Checkpoint newCp = new Checkpoint(
                                (LocalDateTime) last.get("createdAt"),
                                (String) last.get("id"));
                        saveCheckpoint(checkpointName, newCp);
                    } finally {
                        inFlight.release();
                    }
                }
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        };

        for (int i = 0; i < consumerCount; i++) {
            consumerFutures.add(virtualExecutor.submit(consumerTask));
        }

        // Wait for producer
        producerFuture.get();

        // Wait for all consumers to drain the queue
        for (Future<?> f : consumerFutures) {
            f.get();
        }

        log.info("Migration finished.");
    }

    // --------- DB READ (FAST + STABLE) ----------
    private List<Map<String, Object>> fetchBatch(Checkpoint cp, int limit) {
        // NOTE: created_at and updated_at are LocalDateTime mapped by MySQL driver.
        // If your JDBC returns Timestamp, we convert below.

        String sql = """
                  SELECT
                    p.id            AS id,
                    p.created_at    AS createdAt,
                    p.updated_at    AS updatedAt,
                    p.name          AS name,
                    p.sku           AS sku,
                    p.description   AS description,
                    p.price         AS price,
                    p.discount      AS discount,
                    b.name          AS brandName,
                    c.name          AS categoryName,
                    p.is_active     AS isActive
                  FROM product_service.products p
                  INNER JOIN (
                    SELECT id
                    FROM product_service.products
                    WHERE (created_at > ?)
                       OR (created_at = ? AND id > ?)
                    ORDER BY created_at, id
                    LIMIT ?
                  ) AS test ON p.id = test.id
                  LEFT JOIN product_service.brands b ON p.brand_id = b.id
                  LEFT JOIN product_service.categories c ON c.id = p.category_id
                  ORDER BY p.created_at, p.id
                """;

        return jdbc.query(sql, rs -> {
            List<Map<String, Object>> out = new ArrayList<>(limit);
            while (rs.next()) {
                Map<String, Object> doc = new HashMap<>();

                String id = rs.getString("id");
                doc.put("id", id);

                Object createdObj = rs.getObject("createdAt");
                Object updatedObj = rs.getObject("updatedAt");

                // Convert Timestamp -> LocalDateTime if needed
                LocalDateTime createdAt = toLocalDateTime(createdObj);
                LocalDateTime updatedAt = toLocalDateTime(updatedObj);

                doc.put("createdAt", createdAt);
                doc.put("updatedAt", updatedAt);

                doc.put("name", rs.getString("name"));
                doc.put("sku", rs.getString("sku"));
                doc.put("description", rs.getString("description"));

                BigDecimal price = rs.getBigDecimal("price");
                BigDecimal discount = rs.getBigDecimal("discount");
                doc.put("price", price);
                doc.put("discount", discount);

                doc.put("brandName", rs.getString("brandName"));
                doc.put("categoryName", rs.getString("categoryName"));

                Object isActiveObj = rs.getObject("isActive");
                doc.put("isActive", isActiveObj == null ? null : rs.getBoolean("isActive"));

                out.add(doc);
            }
            return out;
        }, cp.lastCreatedAt, cp.lastCreatedAt, cp.lastId, limit);
    }

    private LocalDateTime toLocalDateTime(Object jdbcObj) {
        if (jdbcObj == null)
            return null;
        if (jdbcObj instanceof LocalDateTime ldt)
            return ldt;
        if (jdbcObj instanceof java.sql.Timestamp ts)
            return ts.toLocalDateTime();
        if (jdbcObj instanceof java.sql.Date d)
            return d.toLocalDate().atStartOfDay();
        throw new IllegalArgumentException("Unsupported datetime type: " + jdbcObj.getClass());
    }

    // --------- MEILI WRITE ----------
    private void upsertToMeili(List<Map<String, Object>> docs) {
        // Send bulk upsert. Meili treats addDocuments as upsert.
        // We keep payload small by sending only needed fields (already selected).
        log.info("Preprocess upsertToMeili with size {}", docs.size());
        String meiliPrimaryKey = "id";
        meiliClient.post()
                .uri("/indexes/{index}/documents?primaryKey={pk}", meiliIndexName, meiliPrimaryKey)
                .body(docs)
                .retrieve()
                .toBodilessEntity();
    }

    // --------- CHECKPOINT (MySQL table) ----------
    private void ensureCheckpointTable() {
        jdbc.execute("""
                  CREATE TABLE IF NOT EXISTS product_service.meili_migration_checkpoint (
                    name VARCHAR(128) PRIMARY KEY,
                    last_created_at DATETIME NOT NULL,
                    last_id VARCHAR(36) NOT NULL,
                    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                      ON UPDATE CURRENT_TIMESTAMP
                  )
                """);

        // Ensure row exists
        Integer cnt = jdbc.queryForObject("""
                  SELECT COUNT(*) FROM product_service.meili_migration_checkpoint WHERE name=?
                """, Integer.class, checkpointName);

        if (cnt != null && cnt == 0) {
            // Start from "lowest possible" created_at.
            jdbc.update("""
                      INSERT INTO product_service.meili_migration_checkpoint(name, last_created_at, last_id)
                      VALUES (?, '1970-01-01 00:00:00', '')
                    """, checkpointName);
        }
    }

    private Checkpoint loadCheckpoint(String name) {
        Map<String, Object> row = jdbc.queryForMap("""
                  SELECT last_created_at, last_id
                  FROM product_service.meili_migration_checkpoint
                  WHERE name=?
                """, name);

        LocalDateTime lastCreatedAt = toLocalDateTime(row.get("last_created_at"));
        String lastId = Objects.toString(row.get("last_id"), "");
        return new Checkpoint(lastCreatedAt, lastId);
    }

    private void saveCheckpoint(String name, Checkpoint cp) {
        jdbc.update("""
                  UPDATE product_service.meili_migration_checkpoint
                  SET last_created_at=?, last_id=?
                  WHERE name=?
                """, cp.lastCreatedAt, cp.lastId, name);
    }

    // --------- INTERNAL TYPES ----------
    private record Checkpoint(LocalDateTime lastCreatedAt, String lastId) {
    }
}
