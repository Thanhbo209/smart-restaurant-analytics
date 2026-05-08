package com.thanhpham.smart_restaurant_analytics.analytics.repository;

import com.thanhpham.smart_restaurant_analytics.analytics.dto.projection.DailyRevenueProjection;
import com.thanhpham.smart_restaurant_analytics.analytics.dto.projection.MonthlyRevenueProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.thanhpham.smart_restaurant_analytics.order.model.Order;

import java.time.LocalDateTime;
import java.util.List;

public interface RevenueAnalyticsRepository extends JpaRepository<Order, Long> {

    @Query(value = """
            SELECT
                TO_CHAR(o.created_at, 'YYYY-MM-DD')      AS date,
                COUNT(o.id)                              AS orderCount,
                COALESCE(SUM(o.final_amount), 0)         AS totalRevenue,
                COALESCE(AVG(o.final_amount), 0)         AS avgOrderValue
            FROM orders o
            WHERE o.status = 'COMPLETED'
              AND o.created_at >= :start
              AND o.created_at <= :end
            GROUP BY TO_CHAR(o.created_at, 'YYYY-MM-DD')
            ORDER BY date ASC
            """, nativeQuery = true)
    List<DailyRevenueProjection> findDailyRevenue(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query(value = """
            SELECT
                TO_CHAR(o.created_at, 'YYYY-MM')         AS month,
                COUNT(o.id)                              AS orderCount,
                COALESCE(SUM(o.final_amount), 0)         AS totalRevenue
            FROM orders o
            WHERE o.status = 'COMPLETED'
              AND EXTRACT(YEAR FROM o.created_at) = :year
            GROUP BY TO_CHAR(o.created_at, 'YYYY-MM')
            ORDER BY month ASC
            """, nativeQuery = true)
    List<MonthlyRevenueProjection> findMonthlyRevenue(@Param("year") int year);
}