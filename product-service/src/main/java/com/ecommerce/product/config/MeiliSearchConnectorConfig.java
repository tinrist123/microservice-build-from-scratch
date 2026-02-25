package com.ecommerce.product.config;

import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MeiliSearchConnectorConfig {

    @Bean
    Client meiliSearchConnector(MeiliSearchIntegrationConfig meiliSearchIntegrationConfig) {
        return new Client(
                new Config(meiliSearchIntegrationConfig.getBaseHost(), meiliSearchIntegrationConfig.getBearerToken())
        );
    }
}
