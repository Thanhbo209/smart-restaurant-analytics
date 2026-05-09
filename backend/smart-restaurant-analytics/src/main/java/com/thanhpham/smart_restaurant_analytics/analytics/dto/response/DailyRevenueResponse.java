package com.thanhpham.smart_restaurant_analytics.analytics.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DailyRevenueResponse(
        Period period,
        List<DailyItem> items,
        Summary summary) {
    public record Period(String from, String to) {
    }

    public record DailyItem(
            String date,
            Long orderCount,
            BigDecimal totalRevenue,
            BigDecimal avgOrderValue) {
    }

    public record Summary(
            BigDecimal totalRevenue,
            Long totalOrders,
            String peakDay // date with highest revenue
    ) {
    }
}
