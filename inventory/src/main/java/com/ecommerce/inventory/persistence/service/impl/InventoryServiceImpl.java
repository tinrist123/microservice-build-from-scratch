package com.ecommerce.inventory.persistence.service.impl;

import com.ecommerce.inventory.persistence.service.InventoryService;
import com.ecommerce.inventory.request.InventoryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    @Override
    public boolean checkInventoryInStock(InventoryDTO inventoryDTO) {
        System.out.println("checkInventoryInStock " + inventoryDTO);

        return false;
    }
}
