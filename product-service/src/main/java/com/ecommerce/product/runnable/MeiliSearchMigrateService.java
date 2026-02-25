package com.ecommerce.product.runnable;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MeiliSearchMigrateService implements CommandLineRunner {

    private final ParallelMeiliMigrator parallelMeiliMigrator;
    @Override
    public void run(String... args) throws Exception {
        parallelMeiliMigrator.migrateOnceFast();
    }
}
