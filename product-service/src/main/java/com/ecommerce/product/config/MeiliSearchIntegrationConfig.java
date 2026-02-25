package com.ecommerce.product.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.meilisearch.setting")
public class MeiliSearchIntegrationConfig {

    private String baseHost;

    private String bearerToken;

    private String indexKey;

}
