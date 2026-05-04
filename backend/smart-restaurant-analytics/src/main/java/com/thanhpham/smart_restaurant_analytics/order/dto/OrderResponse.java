package com.thanhpham.smart_restaurant_analytics.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.thanhpham.smart_restaurant_analytics.order.enums.OrderChannel;
import com.thanhpham.smart_restaurant_analytics.order.enums.OrderStatus;
import com.thanhpham.smart_restaurant_analytics.order.enums.OrderType;
import com.thanhpham.smart_restaurant_analytics.order.model.Order;
import com.thanhpham.smart_restaurant_analytics.payment.enums.PaymentStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderResponse {
    private Long id;
    private OrderType type;
    private OrderChannel channel;
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private String tableNumber;
    private String customerName;
    private String phone;
    private String address;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private String notes;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static OrderResponse from(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .type(order.getType())
                .channel(order.getChannel())
                .status(order.getStatus())
                .paymentStatus(order.getPaymentStatus())
                .tableNumber(order.getTableNumber())
                .customerName(order.getCustomerName())
                .phone(order.getPhone())
                .address(order.getAddress())
                .totalAmount(order.getTotalAmount())
                .discountAmount(order.getDiscountAmount())
                .finalAmount(order.getFinalAmount())
                .notes(order.getNotes())
                // loading items in the generic order response mapper.
                // this turns list endpoints into an N+1 query path and scales payload size with
                // page size.
                .items(order.getItems().stream().map(OrderItemResponse::from).toList())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}