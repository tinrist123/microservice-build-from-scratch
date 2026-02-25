package com.ecommerce.inventory.persistence.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "warehouse")
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "id", nullable = false)),
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at"))
})
public class Warehouse extends CompactAbstractBaseEntity {
    @jakarta.validation.constraints.Size(max = 32)
    @jakarta.validation.constraints.NotNull
    @Column(name = "code", nullable = false, length = 32)
    private String code;

    @jakarta.validation.constraints.Size(max = 255)
    @jakarta.validation.constraints.NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @jakarta.validation.constraints.NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = false;

}