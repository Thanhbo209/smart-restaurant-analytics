package com.thanhpham.smart_restaurant_analytics.analytics.dto.projection;

import java.math.BigDecimal;

public interface BestSellerProjection {
    Long getProductId();

    String getProductName();

    String getCategoryName();

    Long getTotalQuantity();

    BigDecimal getTotalRevenue();

    Long getOrderCount();
}