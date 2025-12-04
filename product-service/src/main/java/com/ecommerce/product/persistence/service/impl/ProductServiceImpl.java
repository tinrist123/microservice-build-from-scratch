package com.ecommerce.product.persistence.service.impl;

import com.ecommerce.product.mapper.ProductMapper;
import com.ecommerce.product.model.request.ProductDTO;
import com.ecommerce.product.persistence.repository.ProductRepository;
import com.ecommerce.product.persistence.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public List<ProductDTO> getProduct(List<String> skus) {
        log.info("request income getProduct: {} - thread : {}", skus, Thread.currentThread().getName());

        List<Integer> dumpMemories = new ArrayList<>();
        for (int i = 0; i < 100_000; i++) {
            dumpMemories.add(i);
        }

        dumpMemories.stream().anyMatch(o -> o.equals(-1));

        return productMapper.toProductDTOS(
                productRepository.findBySkuIn(new HashSet<>(skus))
        );
    }
}
