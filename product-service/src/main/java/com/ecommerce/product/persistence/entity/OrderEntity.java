package com.ecommerce.product.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "orders", schema = "order_service")
public class OrderEntity extends AbstractBaseEntity {

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "order_number", nullable = false, length = 50)
    private String orderNumber;

    @Lob
    @Column(name = "status")
    private String status;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "discount_amount", precision = 12, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "final_amount", precision = 12, scale = 2)
    private BigDecimal finalAmount;

    @Lob
    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Column(name = "shipping_address_id", nullable = false, length = 36)
    private String shippingAddressId;

    @Column(name = "billing_address_id", nullable = false, length = 36)
    private String billingAddressId;

}