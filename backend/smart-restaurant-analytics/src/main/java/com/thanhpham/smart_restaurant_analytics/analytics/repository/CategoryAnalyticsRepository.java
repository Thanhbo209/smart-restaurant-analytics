package com.thanhpham.smart_restaurant_analytics.analytics.repository;

import com.thanhpham.smart_restaurant_analytics.analytics.dto.projection.CategoryRevenueProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.thanhpham.smart_restaurant_analytics.order.model.Order;
import java.time.LocalDateTime;
import java.util.List;

public interface CategoryAnalyticsRepository extends JpaRepository<Order, Long> {

    @Query(value = """
            WITH category_totals AS (
                SELECT
                    c.id                          AS categoryId,
                    c.name                        AS categoryName,
                    COUNT(DISTINCT o.id)          AS orderCount,
                    COALESCE(SUM(oi.subtotal), 0) AS totalRevenue
                FROM order_items oi
                JOIN products    p  ON p.id  = oi.product_id
                JOIN categories  c  ON c.id  = p.category_id
                JOIN orders      o  ON o.id  = oi.order_id
                WHERE o.status = 'COMPLETED'
                  AND o.created_at >= :start
                  AND o.created_at <= :end
                GROUP BY c.id, c.name
            ),
            grand_total AS (
                SELECT SUM(totalRevenue) AS total FROM category_totals
            )
            SELECT
                ct.categoryId,
                ct.categoryName,
                ct.orderCount,
                ct.totalRevenue,
                CASE WHEN gt.total > 0
                     THEN ROUND((ct.totalRevenue / gt.total) * 100, 2)
                     ELSE 0
                END AS revenueShare
            FROM category_totals ct, grand_total gt
            ORDER BY ct.totalRevenue DESC
            """, nativeQuery = true)
    List<CategoryRevenueProjection> findCategoryRevenue(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}