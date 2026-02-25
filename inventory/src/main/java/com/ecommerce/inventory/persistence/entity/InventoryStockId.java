package com.ecommerce.inventory.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class InventoryStockId implements Serializable {
    private static final long serialVersionUID = 2850194716977526412L;
    @jakarta.validation.constraints.NotNull
    @Column(name = "inventory_product_id", nullable = false)
    private Long inventoryProductId;

    @jakarta.validation.constraints.NotNull
    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        InventoryStockId entity = (InventoryStockId) o;
        return Objects.equals(this.warehouseId, entity.warehouseId) &&
                Objects.equals(this.inventoryProductId, entity.inventoryProductId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(warehouseId, inventoryProductId);
    }

}