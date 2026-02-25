package com.ecommerce.product.config.service;

import com.ecommerce.inventory.product.rest.service.ApiClient;
import com.ecommerce.inventory.product.rest.service.v1.InventoryServiceApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class InventoryServiceApiConfig {

    @Value("${app.service.inventory-service.url}")
    private String inventoryServiceUrl;

    @Bean
    public InventoryServiceApi createInventoryServiceApi(RestClient.Builder loadBalancedRestClientBuilder) {
        RestClient restClient = loadBalancedRestClientBuilder.baseUrl(inventoryServiceUrl).build();
        ApiClient apiClient = new ApiClient(restClient);
        return new InventoryServiceApi(apiClient);
    }
}
