package com.thanhpham.smart_restaurant_analytics.order.helper;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.thanhpham.smart_restaurant_analytics.exception.BusinessRuleException;
import com.thanhpham.smart_restaurant_analytics.exception.ResourceNotFoundException;
import com.thanhpham.smart_restaurant_analytics.order.dto.OrderResponse;
import com.thanhpham.smart_restaurant_analytics.order.enums.OrderStatus;
import com.thanhpham.smart_restaurant_analytics.order.model.Order;
import com.thanhpham.smart_restaurant_analytics.order.model.OrderItem;
import com.thanhpham.smart_restaurant_analytics.order.repository.OrderRepository;
import com.thanhpham.smart_restaurant_analytics.order.statemachine.OrderStateMachine;
import com.thanhpham.smart_restaurant_analytics.payment.enums.PaymentStatus;
import com.thanhpham.smart_restaurant_analytics.product.model.Product;
import com.thanhpham.smart_restaurant_analytics.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderHelper {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    private final OrderStateMachine stateMachine;

    public Order findOrThrow(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
    }

    public OrderResponse applyTransition(Long id, OrderStatus next, String label) {
        Order order = findOrThrow(id);
        stateMachine.transition(order, next);
        Order saved = orderRepository.save(order);
        log.info("Order {}: id={}", label, id);

        // Auto-complete: payment already PAID + fulfillment now done
        if (stateMachine.isFulfillmentComplete(saved)
                && saved.getPaymentStatus() == PaymentStatus.PAID
                && saved.getStatus() != OrderStatus.COMPLETED) {
            stateMachine.transition(saved, OrderStatus.COMPLETED);
            orderRepository.save(saved);
            log.info("Order auto-completed after fulfillment: id={}", id);
        }

        return OrderResponse.from(saved);
    }

    public List<OrderItem> buildItems(Order order,
            List<com.thanhpham.smart_restaurant_analytics.order.dto.OrderItemRequest> requests) {

        return requests.stream().map(req -> {
            Product product = productRepository.findByIdWithCategory(req.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", req.getProductId()));

            if (!product.getIsActive()) {
                throw new BusinessRuleException(
                        "Product is not active: " + product.getName());
            }
            if (!product.getIsAvailable()) {
                throw new BusinessRuleException(
                        "Product is currently unavailable: " + product.getName());
            }

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(product.getId());
            item.setProductName(product.getName()); // snapshot
            item.setPrice(product.getPrice()); // snapshot
            item.setQuantity(req.getQuantity());
            item.setSubtotal(product.getPrice()
                    .multiply(BigDecimal.valueOf(req.getQuantity())));
            return item;
        }).toList();
    }
}
