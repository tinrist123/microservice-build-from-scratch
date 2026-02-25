package com.ecommerce.inventory.persistence.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "inventory_product")
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "id", nullable = false)),
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
public class InventoryProduct extends AbstractBaseEntity {

    @Size(max = 36)
    @NotNull
    @Column(name = "product_uuid", nullable = false, length = 36)
    private String productUuid;

    @Size(max = 64)
    @NotNull
    @Column(name = "sku", nullable = false, length = 64)
    private String sku;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = false;

}