package com.ecommerce.product.controller;

import com.ecommerce.product.messagequeue.producer.MessageProducer;
import com.ecommerce.product.model.request.ProductDTO;
import com.ecommerce.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final MessageProducer messageProducer;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductDTO> getProduct(@RequestParam("skus") List<String> skus) {
        return productService.getProduct(skus);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(ProductDTO productDTO) {
        productService.createProduct(productDTO);
    }


    @PostMapping("/send")
    @ResponseStatus(HttpStatus.OK)
    public void sendMessage(@RequestBody ProductDTO productDTO) {
        messageProducer.sendMessage(productDTO.toString());
    }
}
