package com.ecommerce.product.model.dto;

import com.ecommerce.product.rest.model.CompactBrand;
import com.ecommerce.product.rest.model.CompactCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JoiningProduct {

    private String name;

    private String description;

    private BigDecimal price;

    private Integer availableQuantity;

    private @Nullable String sku;

    private @Nullable CategoryDto category;

    private @Nullable BrandDto brand;

    private @Nullable String imageUrl;

    public JoiningProduct(String name, String description, BigDecimal price, String sku,
            String categoryId, String categoryName, String categoryDescription,
            String brandName, String brandDescription, String brandLogoUrl) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.sku = sku;
        this.category = new CategoryDto(categoryId, categoryName, categoryDescription);
        this.brand = new BrandDto(brandName, brandDescription, brandLogoUrl);
    }
}
