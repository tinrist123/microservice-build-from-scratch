package com.ecommerce.inventory.persistence.service;

import com.ecommerce.inventory.request.InventoryDTO;

public interface InventoryService {
    boolean checkInventoryInStock(InventoryDTO inventoryDTO);
}
