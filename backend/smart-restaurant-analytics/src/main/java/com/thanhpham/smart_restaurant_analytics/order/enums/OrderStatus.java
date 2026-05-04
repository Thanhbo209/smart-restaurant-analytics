package com.thanhpham.smart_restaurant_analytics.order.enums;

public enum OrderStatus {
    PENDING,
    CONFIRMED,
    PREPARING,
    READY,
    SERVED, // DINE_IN only
    OUT_FOR_DELIVERY, // DELIVERY only
    DELIVERED, // DELIVERY only
    COMPLETED,
    CANCELLED
}
