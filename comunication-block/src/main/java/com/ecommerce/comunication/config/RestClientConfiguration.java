package com.ecommerce.comunication.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.ReactorClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import reactor.netty.http.client.HttpClient;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.util.concurrent.TimeUnit;

@Configuration
public class RestClientConfiguration {

        @Value("${app.webclient.connect-timeout:5000}")
        private int connectTimeout;

        @Value("${app.webclient.read-timeout:5000}")
        private long readTimeout;

        @Bean
        @LoadBalanced
        @Scope("prototype")
        public RestClient.Builder loadBalancedRestClientBuilder() {
                return RestClient.builder()
                                .requestFactory(new ReactorClientHttpRequestFactory(createHttpClient()));
        }

        private HttpClient createHttpClient() {
                return HttpClient.create()
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
                                .doOnConnected(conn -> conn
                                                .addHandlerLast(new ReadTimeoutHandler(readTimeout,
                                                                TimeUnit.MILLISECONDS))
                                                .addHandlerLast(new WriteTimeoutHandler(readTimeout,
                                                                TimeUnit.MILLISECONDS)));
        }
}
