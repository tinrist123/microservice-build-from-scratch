package com.ecommerce.product.mapper;

import org.mapstruct.MapperConfig;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@MapperConfig
public interface DateTimeMapper {
    default OffsetDateTime map(LocalDateTime value) {
        return value == null ? null : value.atOffset(ZoneOffset.UTC);
    }
}
