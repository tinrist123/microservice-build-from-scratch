package com.ecommerce.inventory.service.impl;

import com.ecommerce.inventory.mapper.InventoryStockMapper;
import com.ecommerce.inventory.persistence.repository.InventoryStockRepository;
import com.ecommerce.inventory.product.rest.model.InventoryResponse;
import com.ecommerce.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryStockMapper inventoryStockMapper;

    private final InventoryStockRepository inventoryStockRepository;

    @Override
    public List<InventoryResponse> getInventoryBySkus(List<String> skus) {
        return inventoryStockMapper.toInventoryResponses(
                inventoryStockRepository.getInventoryStockBySkus(
                        new HashSet<>(skus)
                )
        );
    }
}
