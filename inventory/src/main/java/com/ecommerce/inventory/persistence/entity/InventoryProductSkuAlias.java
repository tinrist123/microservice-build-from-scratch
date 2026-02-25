package com.ecommerce.inventory.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "inventory_product_sku_alias")
public class InventoryProductSkuAlias {
    @Id
    @jakarta.validation.constraints.Size(max = 64)
    @Column(name = "alias_sku", nullable = false, length = 64)
    private String aliasSku;

    @jakarta.validation.constraints.NotNull
    @Column(name = "is_current", nullable = false)
    private Boolean isCurrent = false;

    @Column(name = "created_at")
    private Instant createdAt;

}