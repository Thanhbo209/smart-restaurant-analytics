package com.thanhpham.smart_restaurant_analytics.analytics.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record TopCustomerResponse(
        List<Item> items,
        Integer totalPages,
        Long totalElements) {
    public record Item(
            Integer rank,
            Long customerId,
            String customerName,
            String phone,
            Long orderCount,
            BigDecimal totalSpent,
            BigDecimal avgOrderValue) {
    }
}
