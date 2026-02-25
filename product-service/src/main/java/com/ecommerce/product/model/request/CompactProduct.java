package com.ecommerce.product.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompactProduct implements Serializable {

    private String name;

    private String sku;

    private String description;

    private BigDecimal price;

    private BigDecimal discount;

}
