package com.ecommerce.inventory.service.cachemanager;

import com.ecommerce.inventory.persistence.entity.Warehouse;

import java.util.List;

public interface CacheManagerService {

    public List<Warehouse> getWarehouses();

}
