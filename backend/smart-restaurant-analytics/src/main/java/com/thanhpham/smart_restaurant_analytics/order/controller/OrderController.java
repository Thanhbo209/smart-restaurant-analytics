package com.thanhpham.smart_restaurant_analytics.order.controller;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thanhpham.smart_restaurant_analytics.common.ApiResponse;
import com.thanhpham.smart_restaurant_analytics.order.dto.CreateOrderRequest;
import com.thanhpham.smart_restaurant_analytics.order.dto.OrderResponse;
import com.thanhpham.smart_restaurant_analytics.order.enums.OrderChannel;
import com.thanhpham.smart_restaurant_analytics.order.enums.OrderStatus;
import com.thanhpham.smart_restaurant_analytics.order.enums.OrderType;
import com.thanhpham.smart_restaurant_analytics.order.service.OrderService;
import com.thanhpham.smart_restaurant_analytics.payment.dto.PayOrderRequest;
import com.thanhpham.smart_restaurant_analytics.payment.dto.PaymentResponse;
import com.thanhpham.smart_restaurant_analytics.payment.enums.PaymentStatus;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Validated
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','CASHIER','WAITER')")
    public ResponseEntity<ApiResponse<OrderResponse>> create(
            @RequestBody @Valid CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(orderService.createOrder(request), "Order created"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','CASHIER','WAITER','KITCHEN')")
    public ResponseEntity<ApiResponse<OrderResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrderById(id)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','CASHIER','WAITER','KITCHEN')")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getAll(
            @RequestParam(required = false) OrderType type,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) OrderChannel channel,
            @RequestParam(required = false) PaymentStatus paymentStatus,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        return ResponseEntity.ok(ApiResponse.success(
                orderService.getOrders(type, status, channel, paymentStatus, from, to, pageable)));
    }

    @PostMapping("/{id}/confirm")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','CASHIER','WAITER')")
    public ResponseEntity<ApiResponse<OrderResponse>> confirm(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(orderService.confirmOrder(id), "Order confirmed"));
    }

    @PostMapping("/{id}/start-preparing")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','KITCHEN')")
    public ResponseEntity<ApiResponse<OrderResponse>> startPreparing(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(orderService.startPreparing(id), "Order is being prepared"));
    }

    @PostMapping("/{id}/ready")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','KITCHEN')")
    public ResponseEntity<ApiResponse<OrderResponse>> markReady(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(orderService.markReady(id), "Order is ready"));
    }

    @PostMapping("/{id}/serve")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','WAITER')")
    public ResponseEntity<ApiResponse<OrderResponse>> serve(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(orderService.serveOrder(id), "Order served"));
    }

    @PostMapping("/{id}/out-for-delivery")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','CASHIER')")
    public ResponseEntity<ApiResponse<OrderResponse>> outForDelivery(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(orderService.markOutForDelivery(id), "Order out for delivery"));
    }

    @PostMapping("/{id}/deliver")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','CASHIER')")
    public ResponseEntity<ApiResponse<OrderResponse>> deliver(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(orderService.deliverOrder(id), "Order delivered"));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<OrderResponse>> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(orderService.cancelOrder(id), "Order cancelled"));
    }

    @PostMapping("/{id}/pay")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','CASHIER')")
    public ResponseEntity<ApiResponse<PaymentResponse>> pay(
            @PathVariable Long id,
            @RequestBody @Valid PayOrderRequest request) {
        return ResponseEntity.ok(ApiResponse.success(orderService.payOrder(id, request), "Payment recorded"));
    }

    @GetMapping("/{id}/payments")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','CASHIER')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPayments(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getPayments(id)));
    }
}
