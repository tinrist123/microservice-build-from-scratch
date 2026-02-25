package com.ecommerce.product.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandDto {
    private @Nullable String name;

    private @Nullable String description;

    private @Nullable String logoUrl;
}
