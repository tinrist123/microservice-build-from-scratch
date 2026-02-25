package com.ecommerce.inventory.mapper;

import com.ecommerce.inventory.model.dto.InventoryStockDto;
import com.ecommerce.inventory.product.rest.model.InventoryResponse;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InventoryStockMapper {

    @IterableMapping(qualifiedByName = "toInventoryResponse")
    List<InventoryResponse> toInventoryResponses(List<InventoryStockDto> inventoryStockBySkus);

    @Named("toInventoryResponse")
    @Mapping(target = "sku", source = "sku")
    @Mapping(target = "availableQuantity", source = "availableQuantity")
    InventoryResponse toInventoryResponse(InventoryStockDto inventoryStockDto);
}
