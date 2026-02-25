package com.ecommerce.product.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {

    private @Nullable String code;

    private @Nullable String name;

    private @Nullable String description;
}
