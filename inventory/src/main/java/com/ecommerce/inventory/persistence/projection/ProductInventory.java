package com.ecommerce.inventory.persistence.projection;

public interface ProductInventory {
    String getSku();

    Integer getAvailableQuantity();

}
