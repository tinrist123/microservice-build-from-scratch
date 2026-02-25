package com.ecommerce.inventory.controller;

import com.ecommerce.inventory.product.rest.model.InventoryResponse;
import com.ecommerce.inventory.product.rest.service.InventoryServiceApi;
import com.ecommerce.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InventoryServiceController implements InventoryServiceApi {

    private final InventoryService inventoryService;

    @Override
    public ResponseEntity<List<InventoryResponse>> getInventoryBySkus(List<String> skus) {
        return ResponseEntity.ok(
                inventoryService.getInventoryBySkus(skus)
        );
    }
}
