package com.thanhpham.smart_restaurant_analytics.payment.process;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.thanhpham.smart_restaurant_analytics.exception.BusinessRuleException;
import com.thanhpham.smart_restaurant_analytics.order.model.Order;
import com.thanhpham.smart_restaurant_analytics.payment.enums.PaymentMethod;
import com.thanhpham.smart_restaurant_analytics.payment.enums.PaymentResultStatus;
import com.thanhpham.smart_restaurant_analytics.payment.enums.PaymentStatus;
import com.thanhpham.smart_restaurant_analytics.payment.model.Payment;
import com.thanhpham.smart_restaurant_analytics.payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentProcessor {

    private final PaymentRepository paymentRepository;

    public Payment process(Order order, BigDecimal amount,
            PaymentMethod method,
            String note) {

        if (amount == null || amount.signum() <= 0) {
            throw new BusinessRuleException("Payment amount must be greater than zero.");
        }

        guardAgainstInvalidPayment(order);

        BigDecimal paidSoFar = paymentRepository.sumSuccessfulPaymentsByOrderId(order.getId());
        BigDecimal remaining = order.getFinalAmount().subtract(paidSoFar);
        if (amount.compareTo(remaining) > 0) {
            throw new BusinessRuleException("Payment amount exceeds outstanding balance.");
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setMethod(method);
        payment.setAmount(amount);
        payment.setStatus(PaymentResultStatus.SUCCESS);
        payment.setNote(note);

        Payment saved = paymentRepository.save(payment);

        // Recompute total paid after this payment is persisted
        BigDecimal totalPaid = paymentRepository.sumSuccessfulPaymentsByOrderId(order.getId());
        updatePaymentStatus(order, totalPaid);

        log.info("Payment processed: orderId={}, amount={}, totalPaid={}, paymentStatus={}",
                order.getId(), amount, totalPaid, order.getPaymentStatus());

        return saved;
    }

    // PRIVATE Helper

    private void guardAgainstInvalidPayment(Order order) {
        switch (order.getStatus()) {
            case CANCELLED ->
                throw new BusinessRuleException("Cannot pay a cancelled order.");
            case COMPLETED ->
                throw new BusinessRuleException("Order is already completed.");
            default -> {
                // allowed
            }
        }

        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            throw new BusinessRuleException("Order is already fully paid.");
        }
    }

    private void updatePaymentStatus(Order order, BigDecimal totalPaid) {
        BigDecimal finalAmount = order.getFinalAmount();

        if (totalPaid.compareTo(BigDecimal.ZERO) == 0) {
            order.setPaymentStatus(PaymentStatus.UNPAID);
        } else if (totalPaid.compareTo(finalAmount) < 0) {
            order.setPaymentStatus(PaymentStatus.PARTIALLY_PAID);
        } else {
            // totalPaid >= finalAmount
            order.setPaymentStatus(PaymentStatus.PAID);
        }
    }
}