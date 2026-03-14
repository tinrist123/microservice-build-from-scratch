package com.ecommerce.meilisearch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.meilisearch.setting")
public class MeiliSearchIntegrationConfig {

    private String baseHost;

    private String bearerToken;

    private String indexKey;

}
