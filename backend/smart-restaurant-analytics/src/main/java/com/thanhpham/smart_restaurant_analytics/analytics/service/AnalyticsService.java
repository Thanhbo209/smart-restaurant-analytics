package com.thanhpham.smart_restaurant_analytics.analytics.service;

import com.thanhpham.smart_restaurant_analytics.analytics.dto.projection.*;
import com.thanhpham.smart_restaurant_analytics.analytics.dto.response.*;
import com.thanhpham.smart_restaurant_analytics.analytics.repository.*;
import com.thanhpham.smart_restaurant_analytics.analytics.validator.DateRangeValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {

        private final RevenueAnalyticsRepository revenueRepo;
        private final ProductAnalyticsRepository productRepo;
        private final CategoryAnalyticsRepository categoryRepo;
        private final CustomerAnalyticsRepository customerRepo;
        private final OrderAnalyticsRepository orderRepo;
        private final DateRangeValidator validator;

        // ── 1. Daily revenue ──────────────────────────────────────────────────────

        public DailyRevenueResponse getDailyRevenue(LocalDate startDate, LocalDate endDate) {
                LocalDateTime[] range = validator.validateDailyRange(startDate, endDate);

                List<DailyRevenueProjection> rows = revenueRepo.findDailyRevenue(range[0], range[1]);

                List<DailyRevenueResponse.DailyItem> items = rows.stream()
                                .map(r -> new DailyRevenueResponse.DailyItem(
                                                r.getDate(),
                                                r.getOrderCount(),
                                                r.getTotalRevenue(),
                                                r.getAvgOrderValue()))
                                .toList();

                // Build summary
                BigDecimal totalRevenue = items.stream()
                                .map(DailyRevenueResponse.DailyItem::totalRevenue)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                long totalOrders = items.stream()
                                .mapToLong(DailyRevenueResponse.DailyItem::orderCount)
                                .sum();

                String peakDay = items.stream()
                                .max(java.util.Comparator.comparing(DailyRevenueResponse.DailyItem::totalRevenue))
                                .map(DailyRevenueResponse.DailyItem::date)
                                .orElse(null);

                return new DailyRevenueResponse(
                                new DailyRevenueResponse.Period(startDate.toString(), endDate.toString()),
                                items,
                                new DailyRevenueResponse.Summary(totalRevenue, totalOrders, peakDay));
        }

        // ── 2. Monthly revenue ────────────────────────────────────────────────────

        public MonthlyRevenueResponse getMonthlyRevenue(int year) {
                validator.validateYear(year);

                List<MonthlyRevenueProjection> rows = revenueRepo.findMonthlyRevenue(year);

                // Build a map for quick lookup
                java.util.Map<String, MonthlyRevenueProjection> byMonth = new java.util.HashMap<>();
                for (MonthlyRevenueProjection r : rows) {
                        byMonth.put(r.getMonth(), r);
                }

                List<MonthlyRevenueResponse.MonthItem> months = new ArrayList<>();
                BigDecimal prevRevenue = null;

                for (int m = 1; m <= 12; m++) {
                        String key = String.format("%d-%02d", year, m);
                        MonthlyRevenueProjection proj = byMonth.get(key);

                        BigDecimal revenue = proj != null ? proj.getTotalRevenue() : BigDecimal.ZERO;
                        Long orderCount = proj != null ? proj.getOrderCount() : 0L;

                        BigDecimal growth = null;
                        if (prevRevenue != null && prevRevenue.compareTo(BigDecimal.ZERO) > 0) {
                                growth = revenue.subtract(prevRevenue)
                                                .divide(prevRevenue, 4, RoundingMode.HALF_UP)
                                                .multiply(BigDecimal.valueOf(100))
                                                .setScale(2, RoundingMode.HALF_UP);
                        }

                        months.add(new MonthlyRevenueResponse.MonthItem(
                                        m,
                                        Month.of(m).getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                                        orderCount,
                                        revenue,
                                        growth));

                        prevRevenue = revenue;
                }

                return new MonthlyRevenueResponse(year, months);
        }

        // ── 3. Best sellers by revenue ────────────────────────────────────────────

        public BestSellerResponse getBestSellersByRevenue(
                        LocalDate startDate, LocalDate endDate, Integer limit) {

                LocalDateTime[] range = validator.validateDailyRange(startDate, endDate);
                int lim = validator.clampLimit(limit, 10, 50);

                List<BestSellerProjection> rows = productRepo.findBestSellersByRevenue(range[0], range[1], lim);

                List<BestSellerResponse.Item> items = buildRankedItems(rows);

                return new BestSellerResponse(
                                "revenue",
                                new BestSellerResponse.Period(startDate.toString(), endDate.toString()),
                                items);
        }

        // ── 4. Best sellers by quantity ───────────────────────────────────────────

        public BestSellerResponse getBestSellersByQuantity(
                        LocalDate startDate, LocalDate endDate, Integer limit) {

                LocalDateTime[] range = validator.validateDailyRange(startDate, endDate);
                int lim = validator.clampLimit(limit, 10, 50);

                List<BestSellerProjection> rows = productRepo.findBestSellersByQuantity(range[0], range[1], lim);

                List<BestSellerResponse.Item> items = buildRankedItems(rows);

                return new BestSellerResponse(
                                "quantity",
                                new BestSellerResponse.Period(startDate.toString(), endDate.toString()),
                                items);
        }

        // ── 5. Category revenue ───────────────────────────────────────────────────

        public CategoryRevenueResponse getCategoryRevenue(
                        LocalDate startDate, LocalDate endDate) {

                LocalDateTime[] range = validator.validateDailyRange(startDate, endDate);
                List<CategoryRevenueProjection> rows = categoryRepo.findCategoryRevenue(range[0], range[1]);

                List<CategoryRevenueResponse.Item> items = rows.stream()
                                .map(r -> new CategoryRevenueResponse.Item(
                                                r.getCategoryId(),
                                                r.getCategoryName(),
                                                r.getOrderCount(),
                                                r.getTotalRevenue(),
                                                r.getRevenueShare()))
                                .toList();

                return new CategoryRevenueResponse(
                                new CategoryRevenueResponse.Period(startDate.toString(), endDate.toString()),
                                items);
        }

        // ── 6. Top customers ──────────────────────────────────────────────────────

        public TopCustomerResponse getTopCustomers(
                        LocalDate startDate, LocalDate endDate, int page, int size) {

                LocalDateTime[] range = validator.validateDailyRange(startDate, endDate);
                int effectiveSize = Math.min(size, 100);
                Pageable pageable = PageRequest.of(page, effectiveSize);

                Page<TopCustomerProjection> result = customerRepo.findTopCustomers(range[0], range[1], pageable);

                List<TopCustomerResponse.Item> items = new ArrayList<>();
                int rank = page * effectiveSize + 1;
                for (TopCustomerProjection r : result.getContent()) {
                        items.add(new TopCustomerResponse.Item(
                                        rank++,
                                        r.getCustomerId(),
                                        r.getCustomerName(),
                                        r.getPhone(),
                                        r.getOrderCount(),
                                        r.getTotalSpent(),
                                        r.getAvgOrderValue()));
                }

                return new TopCustomerResponse(items, result.getTotalPages(), result.getTotalElements());
        }

        // ── 7. Order summary ──────────────────────────────────────────────────────

        public OrderSummaryResponse getOrderSummary() {
                List<OrderSummaryProjection> rows = orderRepo.findOrderSummaryByStatus();

                List<OrderSummaryResponse.Item> items = rows.stream()
                                .map(r -> new OrderSummaryResponse.Item(
                                                r.getStatus(),
                                                r.getOrderCount(),
                                                r.getTotalRevenue()))
                                .toList();

                BigDecimal grandTotal = items.stream()
                                .map(OrderSummaryResponse.Item::totalRevenue)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                long grandCount = items.stream()
                                .mapToLong(OrderSummaryResponse.Item::orderCount)
                                .sum();

                return new OrderSummaryResponse(items, grandTotal, grandCount);
        }

        // ── 8. Margins ────────────────────────────────────────────────────────────

        public org.springframework.data.domain.Page<MarginProjection> getMargins(
                        Long categoryId, Double minMargin, Pageable pageable) {
                return productRepo.findMargins(categoryId, minMargin, pageable);
        }

        // ── 9. Availability snapshot ──────────────────────────────────────────────

        public java.util.Map<String, Object> getAvailabilitySnapshot() {
                var summary = productRepo.findAvailabilitySummary();
                var lowStock = productRepo.findLowStockProducts(5);

                java.util.Map<String, Object> result = new java.util.LinkedHashMap<>();
                result.put("totalProducts", summary.getTotalProducts());
                result.put("activeProducts", summary.getActiveProducts());
                result.put("availableProducts", summary.getAvailableProducts());
                result.put("unavailableProducts", summary.getUnavailableProducts());
                result.put("outOfStock", summary.getOutOfStock());
                result.put("lowStock", lowStock.stream()
                                .map(i -> java.util.Map.of(
                                                "productId", i.getProductId(),
                                                "productName", i.getProductName(),
                                                "stock", i.getStock()))
                                .toList());
                return result;
        }

        // ── Private helpers ───────────────────────────────────────────────────────

        private List<BestSellerResponse.Item> buildRankedItems(List<BestSellerProjection> rows) {
                List<BestSellerResponse.Item> items = new ArrayList<>();
                int rank = 1;
                for (BestSellerProjection r : rows) {
                        items.add(new BestSellerResponse.Item(
                                        rank++,
                                        r.getProductId(),
                                        r.getProductName(),
                                        r.getCategoryName(),
                                        r.getTotalQuantity(),
                                        r.getTotalRevenue(),
                                        r.getOrderCount()));
                }
                return items;
        }
}
