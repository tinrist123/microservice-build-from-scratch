package com.ecommerce.inventory.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "inventory_stock")
public class InventoryStock {
    @EmbeddedId
    private InventoryStockId id;

    @jakarta.validation.constraints.NotNull
    @Column(name = "quantity_on_hand", nullable = false)
    private Integer quantityOnHand;

    @jakarta.validation.constraints.NotNull
    @Column(name = "quantity_reserved", nullable = false)
    private Integer quantityReserved;

    @Column(name = "quantity_available", insertable = false, updatable = false)
    private Integer availableQuantity;

    @Column(name = "updated_at")
    private Instant updatedAt;

}