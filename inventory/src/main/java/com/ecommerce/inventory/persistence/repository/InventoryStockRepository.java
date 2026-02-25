package com.ecommerce.inventory.persistence.repository;

import com.ecommerce.inventory.model.dto.InventoryStockDto;
import com.ecommerce.inventory.persistence.entity.InventoryStock;
import com.ecommerce.inventory.persistence.entity.InventoryStockId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface InventoryStockRepository extends JpaRepository<InventoryStock, InventoryStockId> {

    @Query(value = """
                SELECT new com.ecommerce.inventory.model.dto.InventoryStockDto(
                is.id.inventoryProductId, ip.sku, is.id.warehouseId ,is.availableQuantity
                )
                from InventoryStock is
                join InventoryProduct ip
                on is.id.inventoryProductId = ip.id
                where ip.sku in :skus
                and ip.isActive = true
                and is.availableQuantity > 0
            """)
    List<InventoryStockDto> getInventoryStockBySkus(@Param("skus") Set<String> skus);

}