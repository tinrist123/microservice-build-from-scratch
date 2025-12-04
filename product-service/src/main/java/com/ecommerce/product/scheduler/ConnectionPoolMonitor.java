package com.ecommerce.product.scheduler;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ConnectionPoolMonitor {
    private final HikariDataSource hikariDataSource;

    public ConnectionPoolMonitor(HikariDataSource hikariDataSource) {
        this.hikariDataSource = hikariDataSource;
    }

    /**
     * Logs HikariCP pool stats every 5 seconds.
     */
    @Scheduled(fixedRate = 10000)
    public void logHikariPoolStats() {
        try {
            HikariPoolMXBean poolProxy = hikariDataSource.getHikariPoolMXBean();
            if (poolProxy != null) {
                int active = poolProxy.getActiveConnections();
                int idle = poolProxy.getIdleConnections();
                int total = poolProxy.getTotalConnections();
                int pending = poolProxy.getThreadsAwaitingConnection();

                log.info("🔹 HikariCP Pool [{}]: active={}, idle={}, total={}, waiting={}",
                        hikariDataSource.getPoolName(), active, idle, total, pending);
            } else {
                log.warn("⚠️ Could not access HikariPoolMXBean (pool may not be initialized yet)");
            }
        } catch (Exception ex) {
            log.error("❌ Error while logging HikariCP pool stats", ex);
        }
    }
}
