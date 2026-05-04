package com.thanhpham.smart_restaurant_analytics.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.thanhpham.smart_restaurant_analytics.payment.enums.PaymentMethod;
import com.thanhpham.smart_restaurant_analytics.payment.enums.PaymentResultStatus;
import com.thanhpham.smart_restaurant_analytics.payment.model.Payment;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentResponse {
    private Long id;
    private PaymentMethod method;
    private BigDecimal amount;
    private PaymentResultStatus status;
    private LocalDateTime paidAt;
    private String note;

    public static PaymentResponse from(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .method(payment.getMethod())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .paidAt(payment.getPaidAt())
                .note(payment.getNote())
                .build();
    }
}
