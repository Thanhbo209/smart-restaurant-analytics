package com.thanhpham.smart_restaurant_analytics.order.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thanhpham.smart_restaurant_analytics.exception.BusinessRuleException;
import com.thanhpham.smart_restaurant_analytics.exception.ResourceNotFoundException;
import com.thanhpham.smart_restaurant_analytics.order.dto.CreateOrderRequest;
import com.thanhpham.smart_restaurant_analytics.order.dto.OrderResponse;
import com.thanhpham.smart_restaurant_analytics.order.enums.OrderChannel;
import com.thanhpham.smart_restaurant_analytics.order.enums.OrderStatus;
import com.thanhpham.smart_restaurant_analytics.order.enums.OrderType;
import com.thanhpham.smart_restaurant_analytics.order.helper.OrderHelper;
import com.thanhpham.smart_restaurant_analytics.order.model.Order;
import com.thanhpham.smart_restaurant_analytics.order.model.OrderItem;
import com.thanhpham.smart_restaurant_analytics.order.repository.OrderRepository;
import com.thanhpham.smart_restaurant_analytics.order.specification.OrderSpecification;
import com.thanhpham.smart_restaurant_analytics.order.statemachine.OrderStateMachine;
import com.thanhpham.smart_restaurant_analytics.order.validator.OrderValidator;
import com.thanhpham.smart_restaurant_analytics.payment.dto.PayOrderRequest;
import com.thanhpham.smart_restaurant_analytics.payment.dto.PaymentResponse;
import com.thanhpham.smart_restaurant_analytics.payment.enums.PaymentStatus;
import com.thanhpham.smart_restaurant_analytics.payment.model.Payment;
import com.thanhpham.smart_restaurant_analytics.payment.process.PaymentProcessor;
import com.thanhpham.smart_restaurant_analytics.payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final OrderValidator orderValidator;
    private final OrderStateMachine stateMachine;
    private final PaymentProcessor paymentProcessor;
    private final OrderHelper helper;

    // --- POST ---------------------------------------------------------

    public OrderResponse createOrder(CreateOrderRequest request) {
        // Validate domain constraints
        orderValidator.validateCreateRequest(request);

        Order order = new Order();
        order.setType(request.getType());
        order.setChannel(request.getChannel());
        order.setTableNumber(request.getTableNumber());
        order.setCustomerName(request.getCustomerName());
        order.setPhone(request.getPhone());
        order.setAddress(request.getAddress());
        order.setNotes(request.getNotes());
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.UNPAID);
        BigDecimal discount = request.getDiscountAmount() != null
                ? request.getDiscountAmount()
                : BigDecimal.ZERO;
        order.setDiscountAmount(discount);

        // Build items and compute totals
        List<OrderItem> items = helper.buildItems(order, request.getItems());
        order.setItems(items);

        BigDecimal total = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal finalAmount = total.subtract(discount).max(BigDecimal.ZERO);
        order.setTotalAmount(total);
        order.setFinalAmount(finalAmount);

        Order savedOrder = orderRepository.save(order);
        log.info("Order created: id={}, type={}, channel={}, finalAmount={}", savedOrder.getId(), savedOrder.getType(),
                savedOrder.getChannel(), savedOrder.getFinalAmount());
        return OrderResponse.from(savedOrder);
    }

    // --- GET ---------------------------------------------------------

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        return OrderResponse.from(helper.findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrders(
            OrderType type,
            OrderStatus status,
            OrderChannel channel,
            PaymentStatus paymentStatus,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable) {

        Specification<Order> spec = OrderSpecification.withFilters(
                type, status, channel, paymentStatus, from, to);

        return orderRepository.findAll(spec, pageable).map(OrderResponse::from);
    }

    // --- STATUS TRANSITIONS --------------------------------------

    public OrderResponse confirmOrder(Long id) {
        return helper.applyTransition(id, OrderStatus.CONFIRMED, "confirmed");
    }

    public OrderResponse startPreparing(Long id) {
        return helper.applyTransition(id, OrderStatus.PREPARING, "preparing");
    }

    public OrderResponse markReady(Long id) {
        return helper.applyTransition(id, OrderStatus.READY, "ready");
    }

    public OrderResponse serveOrder(Long id) {
        return helper.applyTransition(id, OrderStatus.SERVED, "served");
    }

    public OrderResponse markOutForDelivery(Long id) {
        return helper.applyTransition(id, OrderStatus.OUT_FOR_DELIVERY, "out for delivery");
    }

    public OrderResponse deliverOrder(Long id) {
        return helper.applyTransition(id, OrderStatus.DELIVERED, "delivered");
    }

    public OrderResponse cancelOrder(Long id) {
        Order order = orderRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        if (paymentRepository.existsByOrderId(id)) {
            throw new BusinessRuleException(
                    "Cannot cancel order with existing payment records. Issue a refund instead.");
        }
        stateMachine.transition(order, OrderStatus.CANCELLED);
        return OrderResponse.from(orderRepository.save(order));
    }

    // --- PAYMENT ---------------------------------------------------------

    public PaymentResponse payOrder(Long orderId, PayOrderRequest request) {
        Order order = orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        Payment payment = paymentProcessor.process(
                order, request.getAmount(), request.getMethod(), request.getNote());

        // Auto-complete: fulfillment done + now PAID
        if (stateMachine.isFulfillmentComplete(order)
                && order.getPaymentStatus() == PaymentStatus.PAID) {
            stateMachine.transition(order, OrderStatus.COMPLETED);
            log.info("Order auto-completed after payment: id={}", orderId);
        }

        orderRepository.save(order);
        return PaymentResponse.from(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPayments(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException("Order", orderId);
        }
        return paymentRepository.findAllByOrderId(orderId)
                .stream()
                .map(PaymentResponse::from)
                .toList();
    }

}
