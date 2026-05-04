package com.thanhpham.smart_restaurant_analytics.order.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.thanhpham.smart_restaurant_analytics.common.BaseEntity;
import com.thanhpham.smart_restaurant_analytics.order.enums.OrderChannel;
import com.thanhpham.smart_restaurant_analytics.order.enums.OrderStatus;
import com.thanhpham.smart_restaurant_analytics.order.enums.OrderType;
import com.thanhpham.smart_restaurant_analytics.payment.enums.PaymentStatus;
import com.thanhpham.smart_restaurant_analytics.payment.model.Payment;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status = OrderStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    // DINE_IN: required. Others: null.
    @Column(length = 20)
    private String tableNumber;

    @Column(length = 200)
    private String customerName;

    @Column(length = 30)
    private String phone;

    // DELIVERY: required. Others: null.
    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal finalAmount = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = false, fetch = FetchType.LAZY)
    private List<Payment> payments = new ArrayList<>();

}
