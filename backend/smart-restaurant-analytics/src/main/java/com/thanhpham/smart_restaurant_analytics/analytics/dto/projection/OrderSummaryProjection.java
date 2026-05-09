package com.thanhpham.smart_restaurant_analytics.analytics.dto.projection;

import java.math.BigDecimal;

public interface OrderSummaryProjection {
    String getStatus();

    Long getOrderCount();

    BigDecimal getTotalRevenue();
}