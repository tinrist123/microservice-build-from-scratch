package com.ecommerce.inventory.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryStockDto {

    private Long inventoryProductId;

    private String sku;

    private Long wareHouseId;

    private Integer availableQuantity;
}
