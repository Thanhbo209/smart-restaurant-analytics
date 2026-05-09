package com.thanhpham.smart_restaurant_analytics.analytics.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record BestSellerResponse(
        String metric, // "revenue" or "quantity"
        Period period,
        List<Item> items) {
    public record Period(String from, String to) {
    }

    public record Item(
            Integer rank,
            Long productId,
            String productName,
            String categoryName,
            Long totalQuantity,
            BigDecimal totalRevenue,
            Long orderCount) {
    }
}