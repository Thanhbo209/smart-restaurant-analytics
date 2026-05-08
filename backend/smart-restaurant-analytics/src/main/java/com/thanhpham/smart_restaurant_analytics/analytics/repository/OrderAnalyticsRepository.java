package com.thanhpham.smart_restaurant_analytics.analytics.repository;

import com.thanhpham.smart_restaurant_analytics.analytics.dto.projection.OrderSummaryProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.thanhpham.smart_restaurant_analytics.order.model.Order;
import java.util.List;

public interface OrderAnalyticsRepository extends JpaRepository<Order, Long> {

    @Query(value = """
            SELECT
                o.status AS status,
                COUNT(o.id) AS orderCount,
                COALESCE(SUM(
                    CASE
                        WHEN o.status = 'COMPLETED'
                        THEN o.final_amount
                        ELSE 0
                    END
                ), 0) AS totalRevenue
            FROM orders o
            GROUP BY o.status
            ORDER BY orderCount DESC
            """, nativeQuery = true)
    List<OrderSummaryProjection> findOrderSummaryByStatus();
}