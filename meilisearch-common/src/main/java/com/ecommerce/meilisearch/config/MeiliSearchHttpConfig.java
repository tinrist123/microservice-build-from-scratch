package com.ecommerce.meilisearch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.meilisearch.http")
public class MeiliSearchHttpConfig {

    private Timeout timeout = new Timeout();
    private Pool pool = new Pool();

    @Data
    public static class Timeout {
        /** Milliseconds to establish a TCP connection */
        private long connect = 3000;
        /** Milliseconds to wait for response data (socket/read timeout) */
        private long read = 5000;
        /** Milliseconds to wait for a connection from the pool */
        private long connectionRequest = 2000;
    }

    @Data
    public static class Pool {
        /** Max total connections across all routes */
        private int maxTotal = 50;
        /** Max connections per single host/route */
        private int maxPerRoute = 20;
        /** Seconds before idle connections are evicted */
        private long idleEvictSeconds = 30;
    }
}
