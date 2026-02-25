package com.ecommerce.product.service.cachemanager;

import com.ecommerce.product.model.request.CompactProduct;

import java.util.Optional;

public interface CacheManagerService {

    CompactProduct getProductBySku(String sku);
}
