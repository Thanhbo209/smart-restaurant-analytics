package com.thanhpham.smart_restaurant_analytics.order.specification;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;

import com.thanhpham.smart_restaurant_analytics.order.enums.OrderChannel;
import com.thanhpham.smart_restaurant_analytics.order.enums.OrderStatus;
import com.thanhpham.smart_restaurant_analytics.order.enums.OrderType;
import com.thanhpham.smart_restaurant_analytics.order.model.Order;
import com.thanhpham.smart_restaurant_analytics.payment.enums.PaymentStatus;

public final class OrderSpecification {

    private OrderSpecification() {
    }

    public static Specification<Order> hasType(OrderType type) {
        return (root, query, cb) -> type == null ? cb.conjunction() : cb.equal(root.get("type"), type);
    }

    public static Specification<Order> hasStatus(OrderStatus status) {
        return (root, query, cb) -> status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }

    public static Specification<Order> hasChannel(OrderChannel channel) {
        return (root, query, cb) -> channel == null ? cb.conjunction() : cb.equal(root.get("channel"), channel);
    }

    public static Specification<Order> hasPaymentStatus(PaymentStatus paymentStatus) {
        return (root, query, cb) -> paymentStatus == null ? cb.conjunction()
                : cb.equal(root.get("paymentStatus"), paymentStatus);
    }

    public static Specification<Order> createdAfter(LocalDateTime from) {
        return (root, query, cb) -> from == null ? cb.conjunction()
                : cb.greaterThanOrEqualTo(root.get("createdAt"), from);
    }

    public static Specification<Order> createdBefore(LocalDateTime to) {
        return (root, query, cb) -> to == null ? cb.conjunction()
                : cb.lessThanOrEqualTo(root.get("createdAt"), to);
    }

    // Compose all active filters into one Specification
    public static Specification<Order> withFilters(
            OrderType type,
            OrderStatus status,
            OrderChannel channel,
            PaymentStatus paymentStatus,
            LocalDateTime from,
            LocalDateTime to) {

        return Specification
                .where(hasType(type))
                .and(hasStatus(status))
                .and(hasChannel(channel))
                .and(hasPaymentStatus(paymentStatus))
                .and(createdAfter(from))
                .and(createdBefore(to));
    }
}
