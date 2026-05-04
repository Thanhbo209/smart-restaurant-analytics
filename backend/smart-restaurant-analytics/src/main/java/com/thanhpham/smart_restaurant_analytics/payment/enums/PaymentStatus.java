package com.thanhpham.smart_restaurant_analytics.payment.enums;

public enum PaymentStatus {
    UNPAID,
    PARTIALLY_PAID, // sum(payments) > 0 but < finalAmount (not fit the final total price)
    PAID,
    REFUNDED
}