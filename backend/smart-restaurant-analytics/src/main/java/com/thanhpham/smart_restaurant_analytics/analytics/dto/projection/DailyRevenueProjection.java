package com.thanhpham.smart_restaurant_analytics.analytics.dto.projection;

import java.math.BigDecimal;

public interface DailyRevenueProjection {
    String getDate(); // DATE cast → String, formatted as yyyy-MM-dd

    Long getOrderCount();

    BigDecimal getTotalRevenue();

    BigDecimal getAvgOrderValue();
}