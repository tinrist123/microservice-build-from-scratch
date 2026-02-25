package com.common_logging.logging.library.configuration;

import io.micrometer.tracing.Tracer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Auto-configuration that provides a virtual-thread-backed
 * {@link ExecutorService}
 * with automatic MDC and Micrometer tracing context propagation.
 * <p>
 * Inject the bean as:
 * 
 * <pre>
 * &#64;Qualifier("contextAwareVirtualExecutor")
 * ExecutorService executor;
 * </pre>
 */
@Configuration
public class VirtualThreadAutoConfiguration {

    @Bean
    public ExecutorService contextAwareVirtualExecutor(ObjectProvider<Tracer> tracerProvider) {
        Tracer tracer = tracerProvider.getIfAvailable();
        ExecutorService virtualExecutor = Executors.newVirtualThreadPerTaskExecutor();
        return new ContextAwareExecutorService(virtualExecutor, tracer);
    }
}
