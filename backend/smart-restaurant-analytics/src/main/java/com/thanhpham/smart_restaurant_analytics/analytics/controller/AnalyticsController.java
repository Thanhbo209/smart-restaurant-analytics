package com.thanhpham.smart_restaurant_analytics.analytics.controller;

import com.thanhpham.smart_restaurant_analytics.analytics.dto.response.*;
import com.thanhpham.smart_restaurant_analytics.analytics.service.AnalyticsService;
import com.thanhpham.smart_restaurant_analytics.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')") // class-level guard
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    // ── Revenue endpoints ─────────────────────────────────────────────────────

    @GetMapping("/revenue/daily")
    public ResponseEntity<ApiResponse<DailyRevenueResponse>> getDailyRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return ResponseEntity.ok(
                ApiResponse.success(analyticsService.getDailyRevenue(startDate, endDate)));
    }

    @GetMapping("/revenue/monthly")
    public ResponseEntity<ApiResponse<MonthlyRevenueResponse>> getMonthlyRevenue(
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") int year) {

        return ResponseEntity.ok(
                ApiResponse.success(analyticsService.getMonthlyRevenue(year)));
    }

    // ── Product endpoints ─────────────────────────────────────────────────────

    @GetMapping("/products/best-sellers")
    public ResponseEntity<ApiResponse<BestSellerResponse>> getBestSellersByRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") Integer limit) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        analyticsService.getBestSellersByRevenue(startDate, endDate, limit)));
    }

    @GetMapping("/products/best-sellers/by-quantity")
    public ResponseEntity<ApiResponse<BestSellerResponse>> getBestSellersByQuantity(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") Integer limit) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        analyticsService.getBestSellersByQuantity(startDate, endDate, limit)));
    }

    @GetMapping("/products/margins")
    public ResponseEntity<ApiResponse<?>> getMargins(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Double minMargin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        analyticsService.getMargins(categoryId, minMargin,
                                PageRequest.of(page, Math.min(size, 100)))));
    }

    @GetMapping("/products/availability")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAvailability() {
        return ResponseEntity.ok(
                ApiResponse.success(analyticsService.getAvailabilitySnapshot()));
    }

    // ── Category endpoint ─────────────────────────────────────────────────────

    @GetMapping("/trends/category")
    public ResponseEntity<ApiResponse<CategoryRevenueResponse>> getCategoryRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        analyticsService.getCategoryRevenue(startDate, endDate)));
    }

    // ── Customer endpoint ─────────────────────────────────────────────────────

    @GetMapping("/customers/top")
    public ResponseEntity<ApiResponse<TopCustomerResponse>> getTopCustomers(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        analyticsService.getTopCustomers(startDate, endDate, page, size)));
    }

    // ── Order summary ─────────────────────────────────────────────────────────

    @GetMapping("/orders/summary")
    public ResponseEntity<ApiResponse<OrderSummaryResponse>> getOrderSummary() {
        return ResponseEntity.ok(
                ApiResponse.success(analyticsService.getOrderSummary()));
    }
}