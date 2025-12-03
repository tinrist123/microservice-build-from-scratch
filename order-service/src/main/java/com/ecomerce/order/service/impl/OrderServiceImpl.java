package com.ecomerce.order.service.impl;

import com.ecomerce.order.request.OrderDto;
import com.ecomerce.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final DiscoveryClient discoveryClient;
    private final WebClient webClient;

    @Override
    public void createOrder(OrderDto order) {
        ServiceInstance serviceInstance = discoveryClient.getInstances("inventory-service")
                .getFirst();

        Boolean isAvailable = webClient.post()
                .uri("http://localhost:8080/api/inventories")
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

        System.out.println("order already created");
    }
}
