package com.thanhpham.smart_restaurant_analytics.payment.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.thanhpham.smart_restaurant_analytics.order.model.Order;
import com.thanhpham.smart_restaurant_analytics.payment.enums.PaymentMethod;
import com.thanhpham.smart_restaurant_analytics.payment.enums.PaymentResultStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentMethod method;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentResultStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime paidAt = LocalDateTime.now();

    @Column(length = 500)
    private String note;
}
