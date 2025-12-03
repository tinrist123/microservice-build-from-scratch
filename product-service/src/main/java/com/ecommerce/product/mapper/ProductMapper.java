package com.ecommerce.product.mapper;

import com.ecommerce.product.model.request.ProductDTO;
import com.ecommerce.product.persistence.entity.ProductEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    List<ProductDTO> toProductDTOS(List<ProductEntity> productEntities);
}
