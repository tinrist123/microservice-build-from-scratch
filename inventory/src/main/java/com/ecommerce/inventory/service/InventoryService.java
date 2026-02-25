package com.ecommerce.inventory.service;

import com.ecommerce.inventory.product.rest.model.InventoryResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface InventoryService {
    List<InventoryResponse> getInventoryBySkus(List<String> skus);
}
