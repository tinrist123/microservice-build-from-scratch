package com.common_logging.logging.library.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "common.logging")
@Data
public class LoggingProperties {
    /**
     * Enable request/response logging
     */
    private boolean enabled = true;

    /**
     * Include headers in logs
     */
    private boolean includeHeaders = true;

    /**
     * Include payload bodies in logs
     */
    private boolean includePayload = true;

    /**
     * Max payload length to log (bytes). Longer payloads will be truncated.
     */
    private int maxPayloadLength = 4096;

    /**
     * Paths to exclude from logging (prefix matching)
     */
    private List<String> excludePaths = new ArrayList<>();

    /**
     * Headers to mask (e.g., Authorization)
     */
    private List<String> maskHeaders = List.of("authorization");

    /**
     * Headers to mask (e.g., Authorization)
     */
    private List<String> maskJsonFields = List.of("");
}
