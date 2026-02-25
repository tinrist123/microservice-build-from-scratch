package com.ecommerce.inventory.persistence.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "inventory_transaction")
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "id", nullable = false)),
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at"))
})
public class InventoryTransaction extends CompactAbstractBaseEntity {

    @jakarta.validation.constraints.NotNull
    @Lob
    @Column(name = "transaction_type", nullable = false)
    private String transactionType;

    @jakarta.validation.constraints.NotNull
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @jakarta.validation.constraints.Size(max = 64)
    @Column(name = "reference_id", length = 64)
    private String referenceId;

    @jakarta.validation.constraints.Size(max = 255)
    @Column(name = "note")
    private String note;

}