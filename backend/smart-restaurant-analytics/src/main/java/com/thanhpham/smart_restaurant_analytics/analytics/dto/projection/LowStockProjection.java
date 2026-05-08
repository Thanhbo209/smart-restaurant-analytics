package com.thanhpham.smart_restaurant_analytics.analytics.dto.projection;

public interface LowStockProjection {
    Long getProductId();

    String getProductName();

    Integer getStock();
}
