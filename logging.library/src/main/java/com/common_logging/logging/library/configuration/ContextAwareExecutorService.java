package com.common_logging.logging.library.configuration;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.slf4j.MDC;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Decorator for any {@link ExecutorService} that propagates the parent thread's
 * MDC context and Micrometer tracing span to child threads.
 * <p>
 * Usage:
 * 
 * <pre>
 * ExecutorService raw = Executors.newVirtualThreadPerTaskExecutor();
 * ExecutorService aware = new ContextAwareExecutorService(raw, tracer);
 * aware.submit(() -> {
 *     // MDC + traceId/spanId are available here
 * });
 * </pre>
 */
public class ContextAwareExecutorService implements ExecutorService {

    private final ExecutorService delegate;
    private final Tracer tracer; // nullable

    public ContextAwareExecutorService(ExecutorService delegate, Tracer tracer) {
        this.delegate = delegate;
        this.tracer = tracer;
    }

    // --------- Context wrapping ----------

    private Runnable wrap(Runnable task) {
        Map<String, String> parentMdc = MDC.getCopyOfContextMap();
        Span parentSpan = (tracer != null) ? tracer.currentSpan() : null;

        return () -> {
            // Restore parent MDC
            if (parentMdc != null) {
                MDC.setContextMap(parentMdc);
            }
            // Restore parent tracing span
            Tracer.SpanInScope spanInScope = null;
            if (tracer != null && parentSpan != null) {
                spanInScope = tracer.withSpan(parentSpan);
            }
            try {
                task.run();
            } finally {
                if (spanInScope != null) {
                    spanInScope.close();
                }
                MDC.clear();
            }
        };
    }

    private <T> Callable<T> wrap(Callable<T> task) {
        Map<String, String> parentMdc = MDC.getCopyOfContextMap();
        Span parentSpan = (tracer != null) ? tracer.currentSpan() : null;

        return () -> {
            if (parentMdc != null) {
                MDC.setContextMap(parentMdc);
            }
            Tracer.SpanInScope spanInScope = null;
            if (tracer != null && parentSpan != null) {
                spanInScope = tracer.withSpan(parentSpan);
            }
            try {
                return task.call();
            } finally {
                if (spanInScope != null) {
                    spanInScope.close();
                }
                MDC.clear();
            }
        };
    }

    // --------- Delegated methods with wrapping ----------

    @Override
    public void execute(Runnable command) {
        delegate.execute(wrap(command));
    }

    @Override
    public Future<?> submit(Runnable task) {
        return delegate.submit(wrap(task));
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return delegate.submit(wrap(task), result);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return delegate.submit(wrap(task));
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return delegate.invokeAll(tasks.stream().map(this::wrap).toList());
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException {
        return delegate.invokeAll(tasks.stream().map(this::wrap).toList(), timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
            throws InterruptedException, ExecutionException {
        return delegate.invokeAny(tasks.stream().map(this::wrap).toList());
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return delegate.invokeAny(tasks.stream().map(this::wrap).toList(), timeout, unit);
    }

    // --------- Pure delegation (no wrapping needed) ----------

    @Override
    public void shutdown() {
        delegate.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return delegate.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return delegate.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return delegate.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return delegate.awaitTermination(timeout, unit);
    }

    @Override
    public void close() {
        delegate.close();
    }
}
