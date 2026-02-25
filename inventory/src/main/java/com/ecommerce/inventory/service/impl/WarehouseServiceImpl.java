package com.ecommerce.inventory.service.impl;

import com.ecommerce.inventory.mapper.WarehouseMapper;
import com.ecommerce.inventory.persistence.entity.Warehouse;
import com.ecommerce.inventory.persistence.repository.WarehouseRepository;
import com.ecommerce.inventory.product.rest.model.InventoryResponse;
import com.ecommerce.inventory.service.InventoryService;
import com.ecommerce.inventory.service.WarehouseService;
import com.ecommerce.inventory.service.cachemanager.CacheManagerService;
import com.ecommerce.inventory.warehouse.rest.model.CompactWareHouse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseMapper warehouseMapper;

    private final CacheManagerService cacheManagerService;

    @Override
    public List<CompactWareHouse> getCompactWarehouse() {
        return warehouseMapper.toCompactWarehouses(
                cacheManagerService.getWarehouses()
        );
    }
}
