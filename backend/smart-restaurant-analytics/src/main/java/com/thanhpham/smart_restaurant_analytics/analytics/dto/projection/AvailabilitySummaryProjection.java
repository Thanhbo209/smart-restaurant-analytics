package com.thanhpham.smart_restaurant_analytics.analytics.dto.projection;

public interface AvailabilitySummaryProjection {
    Long getTotalProducts();

    Long getActiveProducts();

    Long getAvailableProducts();

    Long getUnavailableProducts();

    Long getOutOfStock();
}