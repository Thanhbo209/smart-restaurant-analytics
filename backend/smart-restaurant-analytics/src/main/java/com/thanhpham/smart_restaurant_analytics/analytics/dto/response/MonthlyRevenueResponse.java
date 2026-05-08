package com.thanhpham.smart_restaurant_analytics.analytics.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record MonthlyRevenueResponse(
        Integer year,
        List<MonthItem> months) {
    public record MonthItem(
            Integer month,
            String monthName,
            Long orderCount,
            BigDecimal totalRevenue,
            BigDecimal growthPercent // null for first month
    ) {
    }
}