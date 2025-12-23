package com.example.common_cache.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@ConfigurationProperties(prefix = "cache.redis")
@Getter
@Setter
public class RedisProperties {

    /**
     * Redis host
     */
    private String host = "localhost";

    /**
     * Redis port
     */
    private int port = 6379;

    private Boolean enabled;

    private Duration defaultTtl = Duration.ofMinutes(10);
}
