package com.thanhpham.smart_restaurant_analytics.analytics.repository;

import com.thanhpham.smart_restaurant_analytics.analytics.dto.projection.TopCustomerProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.thanhpham.smart_restaurant_analytics.order.model.Order;
import java.time.LocalDateTime;

public interface CustomerAnalyticsRepository extends JpaRepository<Order, Long> {

  @Query(value = """
      SELECT
          NULL                                AS customerId,
          o.customer_name                    AS customerName,
          o.phone                            AS phone,
          COUNT(o.id)                        AS orderCount,
          COALESCE(SUM(o.final_amount), 0)   AS totalSpent,
          COALESCE(AVG(o.final_amount), 0)   AS avgOrderValue
      FROM orders o
      WHERE o.status = 'COMPLETED'
        AND o.created_at >= :start
        AND o.created_at <= :end
      GROUP BY o.customer_name, o.phone
      ORDER BY totalSpent DESC
      """, countQuery = """
      SELECT COUNT(DISTINCT (o.customer_name, o.phone))
      FROM orders o
      WHERE o.status = 'COMPLETED'
        AND o.created_at >= :start
        AND o.created_at <= :end
      """, nativeQuery = true)
  Page<TopCustomerProjection> findTopCustomers(
      @Param("start") LocalDateTime start,
      @Param("end") LocalDateTime end,
      Pageable pageable);
}