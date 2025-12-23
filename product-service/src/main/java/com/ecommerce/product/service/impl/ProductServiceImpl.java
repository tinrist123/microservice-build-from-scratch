package com.ecommerce.product.service.impl;

import com.ecommerce.product.mapper.ProductMapper;
import com.ecommerce.product.model.request.ProductDTO;
import com.ecommerce.product.persistence.repository.ProductRepository;
import com.ecommerce.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    private final ProductRepository productRepository;

    @Override
    public void createProduct(ProductDTO productDTO) {
    }

    @Override
    @Cacheable(
            value = "products-by-skus",
            key = "#skus.stream().sorted().collect(T(java.util.stream.Collectors).joining(','))",
            unless = "#result == null || #result.isEmpty()"
    )
    public List<ProductDTO> getProduct(List<String> skus) {
        return productMapper.toProductDTOS(
                productRepository.findBySkuIn(new HashSet<>(skus)));
    }
}
