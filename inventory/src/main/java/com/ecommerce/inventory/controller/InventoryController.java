package com.ecommerce.inventory.controller;

import com.ecommerce.inventory.persistence.service.InventoryService;
import com.ecommerce.inventory.request.InventoryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inventories")
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public boolean checkInventoryInStock(InventoryDTO inventoryDTO) {
        return inventoryService.checkInventoryInStock(inventoryDTO);
    }
}
