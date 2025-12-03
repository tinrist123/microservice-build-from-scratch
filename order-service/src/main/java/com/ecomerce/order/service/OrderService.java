package com.ecomerce.order.service;

import com.ecomerce.order.request.OrderDto;
import org.springframework.stereotype.Service;

@Service
public interface OrderService {
    void createOrder(OrderDto order);
}
