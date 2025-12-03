package com.ecomerce.order.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class OrderDto {

    private String name;

    private String sku;

    private String description;

    private BigDecimal price;

    private BigDecimal discount;

}
