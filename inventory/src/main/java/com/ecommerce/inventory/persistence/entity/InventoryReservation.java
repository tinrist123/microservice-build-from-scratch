package com.ecommerce.inventory.persistence.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "inventory_reservation")
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "id", nullable = false)),
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at"))
})
public class InventoryReservation extends CompactAbstractBaseEntity {
    @jakarta.validation.constraints.Size(max = 64)
    @jakarta.validation.constraints.NotNull
    @Column(name = "order_id", nullable = false, length = 64)
    private String orderId;

    @jakarta.validation.constraints.NotNull
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @jakarta.validation.constraints.NotNull
    @Lob
    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "expires_at")
    private Instant expiresAt;

}