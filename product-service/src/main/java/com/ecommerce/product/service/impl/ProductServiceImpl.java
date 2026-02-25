package com.ecommerce.product.service.impl;

import com.ecommerce.inventory.product.rest.model.InventoryResponse;
import com.ecommerce.inventory.product.rest.service.v1.InventoryServiceApi;
import com.ecommerce.product.mapper.ProductMapper;
import com.ecommerce.product.model.dto.JoiningProduct;
import com.ecommerce.product.model.request.CompactProduct;
import com.ecommerce.product.persistence.repository.ProductRepository;
import com.ecommerce.product.rest.model.PageResponse;
import com.ecommerce.product.rest.model.ProductPageResponse;
import com.ecommerce.product.rest.model.ProductResponse;
import com.ecommerce.product.service.ProductService;
import com.ecommerce.product.service.cachemanager.CacheManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    private final ProductRepository productRepository;

    private final CacheManagerService cacheManagerService;

    private final InventoryServiceApi inventoryServiceApi;

    @Override
    public ProductResponse getProductBySku(String sku) {

        CompactProduct compactProduct = cacheManagerService.getProductBySku(sku);

        if (Objects.isNull(compactProduct)) {
            return null;
        }

        List<InventoryResponse> inventoryResponses = inventoryServiceApi.getInventoryBySkus(List.of(sku));

        return productMapper.toProductResponse(
                compactProduct,
                Optional.ofNullable(inventoryResponses).orElse(Collections.emptyList())
                        .stream().findFirst().map(InventoryResponse::getAvailableQuantity).orElse(null));
    }

    @Override
    public ProductPageResponse getProducts(String queryText, String category, Pageable pageable) {
        Page<String> skusPage = productRepository.findSkusByLikeQueryText(category, queryText, pageable);

        if (skusPage.isEmpty()) {
            return this.buildNoContentResponse(pageable);
        }

        Map<String, Integer> inventoryMapBySku = inventoryServiceApi.getInventoryBySkus(
                        skusPage.getContent()
                ).stream()
                .filter(inventory -> Objects.nonNull(inventory.getAvailableQuantity()))
                .collect(Collectors.toMap(InventoryResponse::getSku, InventoryResponse::getAvailableQuantity));


        List<JoiningProduct> joiningProducts = productRepository.findProductsBySkus(
                inventoryMapBySku.keySet()
        );

        return productMapper.ProductPageResponse(
                skusPage,
                joiningProducts,
                inventoryMapBySku
        );
    }

    private ProductPageResponse buildNoContentResponse(Pageable pageable) {
        return new ProductPageResponse()
                .pageResponse(
                        new PageResponse()
                                .page(pageable.getPageNumber())
                                .size(pageable.getPageSize())
                );

    }
}
