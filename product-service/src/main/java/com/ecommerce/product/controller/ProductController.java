package com.ecommerce.product.controller;

import com.ecommerce.product.rest.client.v1.ProductClientApi;
import com.ecommerce.product.rest.model.ProductPageResponse;
import com.ecommerce.product.rest.model.ProductResponse;
import com.ecommerce.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ProductController implements ProductClientApi {

    private final ProductService productService;

    @Override
    public ResponseEntity<ProductResponse> getProductBySku(String sku) {
        return Optional.ofNullable(productService.getProductBySku(sku))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<ProductPageResponse> getProducts(String queryText, String category, Pageable pageable) {
        return Optional.ofNullable(productService.getProducts(queryText, category, pageable))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
