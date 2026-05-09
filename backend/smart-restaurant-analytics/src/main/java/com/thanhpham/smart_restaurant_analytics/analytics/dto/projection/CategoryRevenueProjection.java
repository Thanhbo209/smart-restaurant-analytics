package com.thanhpham.smart_restaurant_analytics.analytics.dto.projection;

import java.math.BigDecimal;

public interface CategoryRevenueProjection {
    Long getCategoryId();

    String getCategoryName();

    Long getOrderCount();

    BigDecimal getTotalRevenue();

    BigDecimal getRevenueShare(); // computed as percent in SQL
}