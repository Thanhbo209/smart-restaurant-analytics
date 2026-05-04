package com.thanhpham.smart_restaurant_analytics.order.dto;

import java.math.BigDecimal;
import java.util.List;

import com.thanhpham.smart_restaurant_analytics.order.enums.OrderChannel;
import com.thanhpham.smart_restaurant_analytics.order.enums.OrderType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderRequest {

    @NotNull(message = "Order type is required")
    private OrderType type;

    @NotNull(message = "Order channel is required")
    private OrderChannel channel;

    // Validated in service — context-dependent (required for DINE_IN)
    @Size(max = 20)
    private String tableNumber;

    @Size(max = 200)
    private String customerName;

    @Size(max = 30)
    private String phone;

    // Validated in service — required for DELIVERY
    private String address;

    private BigDecimal discountAmount;

    @Size(max = 1000)
    private String notes;

    @NotEmpty(message = "Order must have at least one item")
    @Valid
    private List<OrderItemRequest> items;
}
