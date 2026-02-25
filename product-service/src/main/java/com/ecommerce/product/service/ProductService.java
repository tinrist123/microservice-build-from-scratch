package com.ecommerce.product.service;

import com.ecommerce.product.rest.model.ProductPageResponse;
import com.ecommerce.product.rest.model.ProductResponse;
import org.springframework.data.domain.Pageable;

public interface ProductService{
    ProductResponse getProductBySku(String sku);
    ProductPageResponse getProducts(String queryText, String category, Pageable pageable);
}
