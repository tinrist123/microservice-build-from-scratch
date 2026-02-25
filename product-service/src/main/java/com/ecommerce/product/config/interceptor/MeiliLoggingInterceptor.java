package com.ecommerce.product.config.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class MeiliLoggingInterceptor implements ClientHttpRequestInterceptor {

    @NotNull
    @Override
    public ClientHttpResponse intercept(@NotNull HttpRequest request, @NotNull byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        logRequest(request, body);
        long start = System.currentTimeMillis();

        ClientHttpResponse response = execution.execute(request, body);

        logResponse(response, System.currentTimeMillis() - start);
        return response;
    }

    private void logRequest(HttpRequest request, byte[] body) {
        log.debug("MeiliSearch Request: {} {} | body size: {} bytes",
                request.getMethod(), request.getURI(), body.length);
        if (log.isTraceEnabled() && body.length > 0) {
            String bodyStr = new String(body, StandardCharsets.UTF_8);
            log.trace("MeiliSearch Request Body: {}",
                    bodyStr.length() > 1000 ? bodyStr.substring(0, 1000) + "... [truncated]" : bodyStr);
        }
    }

    private void logResponse(ClientHttpResponse response, long durationMs) throws IOException {
        log.debug("MeiliSearch Response: {} | took {} ms",
                response.getStatusCode(), durationMs);
        if (log.isTraceEnabled()) {
            String responseBody = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
            log.trace("MeiliSearch Response Body: {}",
                    responseBody.length() > 1000
                            ? responseBody.substring(0, 1000) + "... [truncated]"
                            : responseBody);
        }
    }
}
