package com.ecommerce.product.mapper;

import com.ecommerce.product.model.dto.JoiningProduct;
import com.ecommerce.product.model.request.CompactProduct;
import com.ecommerce.product.persistence.entity.ProductEntity;
import com.ecommerce.product.rest.model.PageResponse;
import com.ecommerce.product.rest.model.ProductPageResponse;
import com.ecommerce.product.rest.model.ProductResponse;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring", config = DateTimeMapper.class)
public interface ProductMapper extends DateTimeMapper {

    CompactProduct toCompactProduct(ProductEntity productEntity);

    @Mapping(target = "availableQuantity", source = "availableStock")
    ProductResponse toProductResponse(CompactProduct productEntity, Integer availableStock);

    @Mapping(target = "availableQuantity", ignore = true)
    ProductResponse toProductResponse(JoiningProduct joiningProduct);

    @Named("enrichContent")
    default List<ProductResponse> enrichContent(List<JoiningProduct> joiningProducts,
            @Context Map<String, Integer> inventoryMapBySku) {
        return joiningProducts.stream()
                .map(product -> {
                    ProductResponse response = toProductResponse(product);
                    response.setAvailableQuantity(inventoryMapBySku.getOrDefault(product.getSku(), 0));
                    return response;
                })
                .toList();
    }

    @Mapping(target = "pageResponse", source = "skusPage")
    @Mapping(target = "content", source = "joiningProducts", qualifiedByName = "enrichContent")
    ProductPageResponse ProductPageResponse(Page<String> skusPage, List<JoiningProduct> joiningProducts,
            @Context Map<String, Integer> inventoryMapBySku);

    @Mapping(target = "page", source = "number")
    @Mapping(target = "size", source = "size")
    @Mapping(target = "totalElements", source = "totalElements")
    @Mapping(target = "totalPages", source = "totalPages")
    @Mapping(target = "first", source = "first")
    @Mapping(target = "last", source = "last")
    @Mapping(target = "empty", source = "empty")
    PageResponse toPageResponse(Page<?> page);
}
