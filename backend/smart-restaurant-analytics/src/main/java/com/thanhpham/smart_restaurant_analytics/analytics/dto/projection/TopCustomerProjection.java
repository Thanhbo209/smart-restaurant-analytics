package com.thanhpham.smart_restaurant_analytics.analytics.dto.projection;

import java.math.BigDecimal;

public interface TopCustomerProjection {
    Long getCustomerId();

    String getCustomerName();

    String getPhone();

    Long getOrderCount();

    BigDecimal getTotalSpent();

    BigDecimal getAvgOrderValue();
}
