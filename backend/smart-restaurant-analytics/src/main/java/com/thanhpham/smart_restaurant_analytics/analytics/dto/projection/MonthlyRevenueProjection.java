package com.thanhpham.smart_restaurant_analytics.analytics.dto.projection;

import java.math.BigDecimal;

public interface MonthlyRevenueProjection {
    String getMonth(); // formatted as yyyy-MM

    Long getOrderCount();

    BigDecimal getTotalRevenue();
}