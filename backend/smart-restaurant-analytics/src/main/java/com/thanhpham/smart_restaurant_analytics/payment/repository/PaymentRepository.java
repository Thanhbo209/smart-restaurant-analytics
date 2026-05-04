package com.thanhpham.smart_restaurant_analytics.payment.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.thanhpham.smart_restaurant_analytics.payment.enums.PaymentResultStatus;
import com.thanhpham.smart_restaurant_analytics.payment.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findAllByOrderId(Long orderId);

    boolean existsByOrderId(Long orderId);

    // Sum of all SUCCESS payments for an order — used to compute payment coverage
    @Query("""
            SELECT COALESCE(SUM(p.amount), 0)
            FROM Payment p
            WHERE p.order.id = :orderId
            AND p.status = :status
            """)
    BigDecimal sumSuccessfulPaymentsByOrderId(
            @Param("orderId") Long orderId,
            @Param("status") PaymentResultStatus status);
}
