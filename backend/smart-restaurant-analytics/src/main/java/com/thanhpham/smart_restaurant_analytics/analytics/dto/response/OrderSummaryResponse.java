package com.thanhpham.smart_restaurant_analytics.analytics.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record OrderSummaryResponse(
        List<Item> items,
        BigDecimal grandTotal,
        Long grandTotalOrders) {
    public record Item(
            String status,
            Long orderCount,
            BigDecimal totalRevenue) {
    }
}