package com.ecommerce.inventory.service.cachemanager.impl;

import com.ecommerce.inventory.persistence.entity.Warehouse;
import com.ecommerce.inventory.persistence.repository.WarehouseRepository;
import com.ecommerce.inventory.service.cachemanager.CacheManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheManagerImpl implements CacheManagerService {

    private final WarehouseRepository warehouseRepository;
    @Override
    @Cacheable(value = "getWarehouses")
    public List<Warehouse> getWarehouses() {
        return warehouseRepository.findWarehousesByIsActive(Boolean.TRUE);
    }
}
