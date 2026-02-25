package com.ecommerce.inventory.mapper;

import com.ecommerce.inventory.persistence.entity.Warehouse;
import com.ecommerce.inventory.warehouse.rest.model.CompactWareHouse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {
    List<CompactWareHouse> toCompactWarehouses(List<Warehouse> warehouses);
}
