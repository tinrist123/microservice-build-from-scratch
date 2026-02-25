package com.ecommerce.inventory.controller;

import com.ecommerce.inventory.product.rest.model.InventoryResponse;
import com.ecommerce.inventory.product.rest.service.InventoryServiceApi;
import com.ecommerce.inventory.service.InventoryService;
import com.ecommerce.inventory.service.WarehouseService;
import com.ecommerce.inventory.warehouse.rest.model.CompactWareHouse;
import com.ecommerce.inventory.warehouse.rest.service.WarehouseServiceApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class WarehouseServiceController implements WarehouseServiceApi {

    private final WarehouseService warehouseService;

    @Override
    public ResponseEntity<List<CompactWareHouse>> getWarehouses() {
        return ResponseEntity.ok(
                warehouseService.getCompactWarehouse()
        );
    }
}
