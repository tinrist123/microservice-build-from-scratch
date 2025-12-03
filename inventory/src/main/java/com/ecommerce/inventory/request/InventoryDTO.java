package com.ecommerce.inventory.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class InventoryDTO {

    private String name;

    private String sku;

    private String description;

    private BigDecimal price;

    private BigDecimal discount;

}
