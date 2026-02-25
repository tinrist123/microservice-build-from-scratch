package com.ecommerce.inventory.persistence.repository;

import com.ecommerce.inventory.persistence.entity.InventoryProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryProductRepository extends JpaRepository<InventoryProduct, Long> {
}