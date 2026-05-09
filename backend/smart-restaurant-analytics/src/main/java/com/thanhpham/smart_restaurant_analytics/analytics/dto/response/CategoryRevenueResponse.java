package com.thanhpham.smart_restaurant_analytics.analytics.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record CategoryRevenueResponse(
        Period period,
        List<Item> items) {
    public record Period(String from, String to) {
    }

    public record Item(
            Long categoryId,
            String categoryName,
            Long orderCount,
            BigDecimal totalRevenue,
            BigDecimal revenueShare) {
    }
}
