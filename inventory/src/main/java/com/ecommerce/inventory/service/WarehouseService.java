package com.ecommerce.inventory.service;

import com.ecommerce.inventory.persistence.entity.Warehouse;
import com.ecommerce.inventory.product.rest.model.InventoryResponse;
import com.ecommerce.inventory.warehouse.rest.model.CompactWareHouse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface WarehouseService {
    List<CompactWareHouse> getCompactWarehouse();
}
