package com.thanhpham.smart_restaurant_analytics.order.statemachine;

import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.thanhpham.smart_restaurant_analytics.exception.BusinessRuleException;
import com.thanhpham.smart_restaurant_analytics.order.enums.OrderStatus;
import com.thanhpham.smart_restaurant_analytics.order.enums.OrderType;
import com.thanhpham.smart_restaurant_analytics.order.model.Order;
import com.thanhpham.smart_restaurant_analytics.payment.enums.PaymentStatus;

@Component
public final class OrderStateMachine {

    private OrderStateMachine() {
    }

    private static final Map<OrderStatus, Set<OrderStatus>> DINE_IN_TRANSITIONS = Map.of(
            OrderStatus.PENDING, Set.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED),
            OrderStatus.CONFIRMED, Set.of(OrderStatus.PREPARING, OrderStatus.CANCELLED),
            OrderStatus.PREPARING, Set.of(OrderStatus.READY),
            OrderStatus.READY, Set.of(OrderStatus.SERVED),
            OrderStatus.SERVED, Set.of(OrderStatus.COMPLETED),
            OrderStatus.COMPLETED, Set.of(),
            OrderStatus.CANCELLED, Set.of());

    private static final Map<OrderStatus, Set<OrderStatus>> DELIVERY_TRANSITIONS = Map.of(
            OrderStatus.PENDING, Set.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED),
            OrderStatus.CONFIRMED, Set.of(OrderStatus.PREPARING, OrderStatus.CANCELLED),
            OrderStatus.PREPARING, Set.of(OrderStatus.READY),
            OrderStatus.READY, Set.of(OrderStatus.OUT_FOR_DELIVERY),
            OrderStatus.OUT_FOR_DELIVERY, Set.of(OrderStatus.DELIVERED),
            OrderStatus.DELIVERED, Set.of(OrderStatus.COMPLETED),
            OrderStatus.COMPLETED, Set.of(),
            OrderStatus.CANCELLED, Set.of());

    private static final Map<OrderStatus, Set<OrderStatus>> TAKEAWAY_TRANSITIONS = Map.of(
            OrderStatus.PENDING, Set.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED),
            OrderStatus.CONFIRMED, Set.of(OrderStatus.PREPARING, OrderStatus.CANCELLED),
            OrderStatus.PREPARING, Set.of(OrderStatus.READY),
            OrderStatus.READY, Set.of(OrderStatus.COMPLETED),
            OrderStatus.COMPLETED, Set.of(),
            OrderStatus.CANCELLED, Set.of());

    public void transition(Order order, OrderStatus next) {
        OrderStatus current = order.getStatus();
        OrderType type = order.getType();

        Set<OrderStatus> allowed = resolveAllowedTransitions(type, current);

        if (!allowed.contains(next)) {
            throw new BusinessRuleException(String.format(
                    "Invalid transition for %s order: %s → %s. Allowed: %s",
                    type, current, next, allowed));
        }

        if (next == OrderStatus.COMPLETED) {
            enforceCompletionRules(order);
        }

        if (next == OrderStatus.CANCELLED) {
            enforceCancellationRules(order);
        }

        order.setStatus(next);
    }

    public boolean isFulfillmentComplete(Order order) {
        return switch (order.getType()) {
            case DINE_IN -> order.getStatus() == OrderStatus.SERVED;
            case DELIVERY -> order.getStatus() == OrderStatus.DELIVERED;
            case TAKEAWAY -> order.getStatus() == OrderStatus.READY;
        };
    }

    // PRIVATE Helper

    private Set<OrderStatus> resolveAllowedTransitions(OrderType type, OrderStatus current) {
        Map<OrderStatus, Set<OrderStatus>> table = switch (type) {
            case DINE_IN -> DINE_IN_TRANSITIONS;
            case DELIVERY -> DELIVERY_TRANSITIONS;
            case TAKEAWAY -> TAKEAWAY_TRANSITIONS;
        };
        return table.getOrDefault(current, Set.of());
    }

    private void enforceCompletionRules(Order order) {
        if (order.getPaymentStatus() != PaymentStatus.PAID) {
            throw new BusinessRuleException(
                    "Cannot complete order: payment status is " + order.getPaymentStatus());
        }

        boolean fulfillmentDone = switch (order.getType()) {
            case DINE_IN -> order.getStatus() == OrderStatus.SERVED;
            case DELIVERY -> order.getStatus() == OrderStatus.DELIVERED;
            case TAKEAWAY -> order.getStatus() == OrderStatus.READY;
        };

        if (!fulfillmentDone) {
            throw new BusinessRuleException(
                    "Cannot complete order: fulfillment not yet finished for type " + order.getType());
        }
    }

    private void enforceCancellationRules(Order order) {
        Set<OrderStatus> nonCancellableStatuses = Set.of(
                OrderStatus.READY,
                OrderStatus.SERVED,
                OrderStatus.OUT_FOR_DELIVERY,
                OrderStatus.DELIVERED,
                OrderStatus.COMPLETED);

        if (nonCancellableStatuses.contains(order.getStatus())) {
            throw new BusinessRuleException(
                    "Cannot cancel order in status: " + order.getStatus());
        }
    }
}
