package com.ecommerce.product.config;

import com.ecommerce.product.config.interceptor.MeiliLoggingInterceptor;

import com.ecommerce.product.config.interceptor.MeiliLoggingInterceptor;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class MeiliSearchRestClientConfig {

    @Bean
    RestClient meiliSearchRestClient(MeiliSearchIntegrationConfig config, MeiliSearchHttpConfig httpConfig) {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(httpConfig.getPool().getMaxTotal());
        connectionManager.setDefaultMaxPerRoute(httpConfig.getPool().getMaxPerRoute());

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofMilliseconds(httpConfig.getTimeout().getConnect()))
                .setResponseTimeout(Timeout.ofMilliseconds(httpConfig.getTimeout().getRead()))
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(httpConfig.getTimeout().getConnectionRequest()))
                .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .evictExpiredConnections()
                .evictIdleConnections(
                        org.apache.hc.core5.util.TimeValue.ofSeconds(
                                httpConfig.getPool().getIdleEvictSeconds()))
                .build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        return RestClient.builder()
                .baseUrl(config.getBaseHost())
                .defaultHeader("Authorization", "Bearer " + config.getBearerToken())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .requestFactory(factory)
                .requestInterceptor(new MeiliLoggingInterceptor())
                .build();
    }
}
