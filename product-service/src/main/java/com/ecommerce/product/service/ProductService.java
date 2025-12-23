package com.ecommerce.product.service;

import com.ecommerce.product.model.request.ProductDTO;

import java.util.List;

public interface ProductService{
    void createProduct(ProductDTO productDTO);

    List<ProductDTO> getProduct(List<String> skus);
}
