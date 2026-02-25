package com.ecommerce.inventory.persistence.repository;

import com.ecommerce.inventory.persistence.entity.InventoryProductSkuAlias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryProductSkuAliasRepository extends JpaRepository<InventoryProductSkuAlias, String> {
}