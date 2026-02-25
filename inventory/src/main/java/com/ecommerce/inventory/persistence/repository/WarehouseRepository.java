package com.ecommerce.inventory.persistence.repository;

import com.ecommerce.inventory.persistence.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    List<Warehouse> findWarehousesByIsActive(Boolean isActive);
}