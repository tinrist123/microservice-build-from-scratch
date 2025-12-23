package com.common_logging.logging.library.filter;

import com.common_logging.logging.library.configuration.LoggingProperties;
import com.common_logging.logging.library.utils.LogMaskingUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Filter that logs request + response with two-phase logging:
 * - PRE: logs request line/headers/body BEFORE chain.doFilter (using a cached-body wrapper)
 * - POST: logs status/response body AFTER chain.doFilter
 * Populates rich MDC for logback; masks sensitive headers/body fields.
 */
public class RequestResponseLoggingFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

    private final LoggingProperties properties;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Tracer tracer; // may be null if Micrometer Tracing not on classpath

    public RequestResponseLoggingFilter(LoggingProperties properties, Tracer tracer) {
        this.properties = properties;
        this.tracer = tracer;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {

        if (!(servletRequest instanceof HttpServletRequest req) ||
                !(servletResponse instanceof HttpServletResponse res) ||
                !properties.isEnabled()) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        // Exclusions
        String path = req.getRequestURI();
        if (isExcluded(path)) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        // Correlation ID
        String requestId = req.getHeader("X-Request-Id");
        if (requestId == null || requestId.isBlank()) requestId = UUID.randomUUID().toString();

        // Wrap request with a cached-body wrapper, so we can read the body now and again later downstream
        CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(req);
        // Wrap response to capture output
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(res);

        // --------- Common derived data & MDC (set before pre-log) ----------
        populateCommonMdc(cachedRequest, requestId);

        long startTime = System.currentTimeMillis();

        try {
            // --------- PRE-REQUEST LOG (before the chain) ----------
            preRequestLog(cachedRequest);

            // Proceed
            chain.doFilter(cachedRequest, wrappedResponse);

        } finally {
            long duration = System.currentTimeMillis() - startTime;
            // --------- POST-RESPONSE LOG ----------
            try {
                postResponseLog(cachedRequest, wrappedResponse, duration);
            } catch (Exception e) {
                log.error("Error during post-response logging", e);
            } finally {
                // Make sure response body is written out
                wrappedResponse.copyBodyToResponse();
                clearRequestMdc();
            }
        }
    }

    /* ====================== PRE / POST loggers ====================== */

    private void preRequestLog(CachedBodyHttpServletRequest request) throws IOException {
        String method = safe(request.getMethod());
        String rawPath = safe(request.getRequestURI());
        String query = safe(request.getQueryString());
        String uri = query.isBlank() ? rawPath : rawPath + "?" + query;

        String headersStr = "";
        if (properties.isIncludeHeaders()) {
            headersStr = Collections.list(request.getHeaderNames()).stream()
                    .map(h -> {
                        String v = request.getHeader(h);
                        if (shouldMaskHeader(h)) v = "****";
                        return h + "=" + v;
                    })
                    .collect(Collectors.joining(", "));
        }

        String reqBody = "";
        if (properties.isIncludePayload()) {
            reqBody = request.getCachedBodyAsString();
            if (!reqBody.isBlank()) {
                reqBody = LogMaskingUtils.maskJsonFields(reqBody, properties.getMaskJsonFields(), objectMapper);
                reqBody = LogMaskingUtils.truncate(reqBody, properties.getMaxPayloadLength());
            }
        }

        // Put MDC extras for logback pattern (also place headers/body if you want them visible in pattern)
        putMdc("reqHeaders", singleLine(headersStr));
        log.info("{}", singleLine(reqBody));
        // message intentionally small; MDC carries structured details
    }

    private void postResponseLog(HttpServletRequest request,
                                 ContentCachingResponseWrapper response,
                                 long durationMs) throws IOException {

        String method = safe(request.getMethod());
        String rawPath = safe(request.getRequestURI());
        String query = safe(request.getQueryString());
        String uri = query.isBlank() ? rawPath : rawPath + "?" + query;

        int status = response.getStatus();

        // Content types & sizes
        String reqContentType = safe(request.getContentType());
        String respContentType = safe(response.getContentType());

        long reqLength = request.getContentLengthLong(); // may be -1
        if (reqLength < 0 && request instanceof CachedBodyHttpServletRequest c) {
            reqLength = c.getCachedBodyLength();
        }
        byte[] respBytes = response.getContentAsByteArray();
        long respLength = (respBytes == null) ? -1 : respBytes.length;

        String respBody = "";
        if (properties.isIncludePayload()) {
            respBody = readResponsePayload(response);
            if (!respBody.isBlank()) {
                respBody = LogMaskingUtils.maskJsonFields(respBody, properties.getMaskJsonFields(), objectMapper);
                respBody = LogMaskingUtils.truncate(respBody, properties.getMaxPayloadLength());
            }
        }

        // Update MDC for post details
        putMdc("status", String.valueOf(status));
        putMdc("durationMs", String.valueOf(durationMs));
        putMdc("reqContentType", reqContentType);
        putMdc("respContentType", respContentType);
        putMdc("reqLength", String.valueOf(reqLength));
        putMdc("respLength", String.valueOf(respLength));

        log.info("{}", singleLine(respBody));    }

    /* ====================== MDC population ====================== */

    private void populateCommonMdc(HttpServletRequest request, String requestId) {
        // Trace IDs (Micrometer if present), with fallbacks
        String traceId = null;
        String spanId  = null;
        try {
            if (this.tracer != null) {
                Span current = this.tracer.currentSpan();
                if (current != null && current.context() != null) {
                    traceId = current.context().traceId();
                    spanId  = current.context().spanId();
                }
            }
            if (traceId == null) traceId = MDC.get("traceId");
            if (spanId  == null) spanId  = MDC.get("spanId");

            if (traceId == null) { // W3C traceparent fallback
                String tp = request.getHeader("traceparent");
                if (tp != null) {
                    String[] parts = tp.split("-");
                    if (parts.length >= 3) {
                        traceId = parts[1];
                        if (spanId == null) spanId = parts[2];
                    }
                }
            }
            if (traceId == null) { // B3 fallbacks
                String b3 = request.getHeader("b3");
                if (b3 != null) {
                    String[] parts = b3.split("-");
                    if (parts.length >= 2) {
                        traceId = parts[0];
                        if (spanId == null) spanId = parts[1];
                    }
                }
            }
            if (traceId == null) traceId = request.getHeader("X-B3-TraceId");
            if (spanId  == null) spanId  = request.getHeader("X-B3-SpanId");
        } catch (Throwable ignored) { }

        String method = safe(request.getMethod());
        String rawPath = safe(request.getRequestURI());
        String query = safe(request.getQueryString());
        String uri = query.isBlank() ? rawPath : rawPath + "?" + query;

        String scheme = safe(request.getScheme());
        String protocol = safe(request.getProtocol());
        String host = safe(request.getServerName());
        String port = String.valueOf(request.getServerPort());

        String userAgent = safe(request.getHeader("User-Agent"));
        String clientIp = resolveClientIp(request);
        String remoteAddr = safe(request.getRemoteAddr());

        // Correlation
        putMdc("requestId", requestId);
        putMdc("traceId", traceId);
        putMdc("spanId", spanId);

        // Request line & connection
        putMdc("httpMethod", method);
        putMdc("path", rawPath);
        putMdc("query", query);
        putMdc("uri", uri);
        putMdc("scheme", scheme);
        putMdc("protocol", protocol);
        putMdc("host", host);
        putMdc("port", port);

        // Client info
        putMdc("clientIp", clientIp);
        putMdc("remoteAddr", remoteAddr);
        putMdc("userAgent", userAgent);
    }

    /* ====================== helpers ====================== */

    private boolean isExcluded(String path) {
        if (properties.getExcludePaths() == null || properties.getExcludePaths().isEmpty()) return false;
        for (String p : properties.getExcludePaths()) {
            if (p != null && !p.isBlank() && path.startsWith(p)) return true;
        }
        return false;
    }

    private boolean shouldMaskHeader(String headerName) {
        if (properties.getMaskHeaders() == null) return false;
        for (String h : properties.getMaskHeaders()) {
            if (headerName.equalsIgnoreCase(h)) return true;
        }
        return false;
    }

    private String readResponsePayload(ContentCachingResponseWrapper response) {
        byte[] buf = response.getContentAsByteArray();
        if (buf == null || buf.length == 0) return "";
        return new String(buf, StandardCharsets.UTF_8);
    }

    private static String safe(String s) {
        return (s == null) ? "" : s;
    }

    private void putMdc(String key, String value) {
        if (value != null) MDC.put(key, value);
    }

    private String resolveClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            int comma = xff.indexOf(',');
            return (comma > 0) ? xff.substring(0, comma).trim() : xff.trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) return realIp.trim();
        return safe(request.getRemoteAddr());
    }

    private void clearRequestMdc() {
        // correlation
        MDC.remove("requestId"); MDC.remove("traceId"); MDC.remove("spanId");
        // request line
        MDC.remove("httpMethod"); MDC.remove("path"); MDC.remove("query"); MDC.remove("uri");
        // connection
        MDC.remove("scheme"); MDC.remove("protocol"); MDC.remove("host"); MDC.remove("port");
        // response
        MDC.remove("status"); MDC.remove("durationMs");
        // client
        MDC.remove("clientIp"); MDC.remove("remoteAddr"); MDC.remove("userAgent");
        // content
        MDC.remove("reqContentType"); MDC.remove("respContentType");
        MDC.remove("reqLength"); MDC.remove("respLength");
        MDC.remove("reqHeaders");
    }

    /* ====================== Cached request body wrapper ====================== */

    /**
     * Reads the entire request body into memory once and allows re-reading by downstream.
     */
    static class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {
        private final byte[] cachedBody;
        private final Charset charset;

        CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
            super(request);
            String enc = request.getCharacterEncoding();
            this.charset = (enc == null || enc.isBlank()) ? StandardCharsets.UTF_8 : Charset.forName(enc);
            this.cachedBody = request.getInputStream().readAllBytes();
        }

        @Override
        public ServletInputStream getInputStream() {
            final ByteArrayInputStream bais = new ByteArrayInputStream(cachedBody);
            return new ServletInputStream() {
                @Override public boolean isFinished() { return bais.available() == 0; }
                @Override public boolean isReady() { return true; }
                @Override public void setReadListener(ReadListener readListener) { /* no-op */ }
                @Override public int read() { return bais.read(); }
            };
        }

        @Override
        public BufferedReader getReader() {
            return new BufferedReader(new InputStreamReader(getInputStream(), charset));
        }

        String getCachedBodyAsString() {
            if (cachedBody == null || cachedBody.length == 0) return "";
            return new String(cachedBody, charset);
        }

        int getCachedBodyLength() {
            return cachedBody == null ? 0 : cachedBody.length;
        }
    }

    private static String singleLine(String s) {
        if (s == null) return null;
        return s.replaceAll("[\\r\\n\\t]+", " ").trim();
    }
}
