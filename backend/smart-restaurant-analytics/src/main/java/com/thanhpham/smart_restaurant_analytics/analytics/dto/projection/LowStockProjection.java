package com.thanhpham.smart_restaurant_analytics.analytics.dto.projection;

public interface LowStockProjection {
    Long getProductId();

    String getProductName();

    // TODO: Implement stock tracking - Issue #TRACK-123
    @org.springframework.lang.Nullable
    Integer getStock();
}
