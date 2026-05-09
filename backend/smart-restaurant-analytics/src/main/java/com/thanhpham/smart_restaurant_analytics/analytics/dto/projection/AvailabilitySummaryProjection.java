package com.thanhpham.smart_restaurant_analytics.analytics.dto.projection;

public interface AvailabilitySummaryProjection {
    Long getTotalProducts();

    Long getActiveProducts();

    Long getAvailableProducts();

    Long getUnavailableProducts();

    // TODO: Implement out-of-stock tracking - Issue #TRACK-123
    @org.springframework.lang.Nullable
    Long getOutOfStock();
}