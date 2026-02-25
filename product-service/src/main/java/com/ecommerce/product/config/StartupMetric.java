package com.ecommerce.product.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class StartupMetric {
    private final Counter restartCounter;

    public StartupMetric(MeterRegistry registry) {
        this.restartCounter = registry.counter("app_restart_count");
    }

    @PostConstruct
    public void bump() {
        restartCounter.increment();
        System.out.println(">>> Prometheus Metric Updated: app_restart_count++");
    }
}
