package com.ecommerce.product.service.cachemanager.impl;

import com.ecommerce.product.mapper.ProductMapper;
import com.ecommerce.product.model.request.CompactProduct;
import com.ecommerce.product.persistence.repository.ProductRepository;
import com.ecommerce.product.service.cachemanager.CacheManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheManagerImpl implements CacheManagerService {

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    @Override
    @Cacheable(value = "getProductBySku", key = "#sku")
    public CompactProduct getProductBySku(String sku) {
        return productRepository.findBySku(sku)
                .map(productMapper::toCompactProduct)
                .orElse(null);
    }
}
