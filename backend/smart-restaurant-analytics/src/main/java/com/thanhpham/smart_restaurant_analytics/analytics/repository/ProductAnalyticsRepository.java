package com.thanhpham.smart_restaurant_analytics.analytics.repository;

import com.thanhpham.smart_restaurant_analytics.analytics.dto.projection.BestSellerProjection;
import com.thanhpham.smart_restaurant_analytics.analytics.dto.projection.MarginProjection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.thanhpham.smart_restaurant_analytics.order.model.Order;
import java.time.LocalDateTime;
import java.util.List;

public interface ProductAnalyticsRepository extends JpaRepository<Order, Long> {

  @Query(value = """
      SELECT
          p.id                                AS productId,
          p.name                              AS productName,
          c.name                              AS categoryName,
          SUM(oi.quantity)                    AS totalQuantity,
          SUM(oi.subtotal)                    AS totalRevenue,
          COUNT(DISTINCT oi.order_id)         AS orderCount
      FROM order_items oi
      JOIN products  p ON p.id = oi.product_id
      JOIN categories c ON c.id = p.category_id
      JOIN orders    o ON o.id = oi.order_id
      WHERE o.status = 'COMPLETED'
        AND o.created_at >= :start
        AND o.created_at <= :end
      GROUP BY p.id, p.name, c.name
      ORDER BY totalRevenue DESC
      LIMIT :lim
      """, nativeQuery = true)
  List<BestSellerProjection> findBestSellersByRevenue(
      @Param("start") LocalDateTime start,
      @Param("end") LocalDateTime end,
      @Param("lim") int limit);

  @Query(value = """
      SELECT
          p.id                                AS productId,
          p.name                              AS productName,
          c.name                              AS categoryName,
          SUM(oi.quantity)                    AS totalQuantity,
          SUM(oi.subtotal)                    AS totalRevenue,
          COUNT(DISTINCT oi.order_id)         AS orderCount
      FROM order_items oi
      JOIN products  p ON p.id = oi.product_id
      JOIN categories c ON c.id = p.category_id
      JOIN orders    o ON o.id = oi.order_id
      WHERE o.status = 'COMPLETED'
        AND o.created_at >= :start
        AND o.created_at <= :end
      GROUP BY p.id, p.name, c.name
      ORDER BY totalQuantity DESC
      LIMIT :lim
      """, nativeQuery = true)
  List<BestSellerProjection> findBestSellersByQuantity(
      @Param("start") LocalDateTime start,
      @Param("end") LocalDateTime end,
      @Param("lim") int limit);

  // ── Margin analytics — no order data needed ────────────────────────────
  @Query(value = """
      SELECT
          p.id                                          AS productId,
          p.name                                        AS productName,
          c.name                                        AS categoryName,
          p.price                                       AS price,
          p.cost                                        AS cost,
          CASE WHEN p.cost IS NOT NULL AND p.cost > 0 AND p.price > 0
               THEN ROUND(((p.price - p.cost) / p.price) * 100, 2)
               ELSE NULL
          END                                           AS marginPercent,
          (p.price - COALESCE(p.cost, 0))               AS marginAmount,
          p.is_available                                AS isAvailable
      FROM products p
      JOIN categories c ON c.id = p.category_id
      WHERE p.is_active = true
        AND (:categoryId IS NULL OR p.category_id = :categoryId)
        AND (:minMargin IS NULL OR
            CASE WHEN p.cost IS NOT NULL AND p.cost > 0 AND p.price > 0
                  THEN ((p.price - p.cost) / p.price) * 100
                  ELSE 0
             END >= :minMargin)
      ORDER BY marginAmount DESC
      """, nativeQuery = true)
  org.springframework.data.domain.Page<MarginProjection> findMargins(
      @Param("categoryId") Long categoryId,
      @Param("minMargin") Double minMargin,
      org.springframework.data.domain.Pageable pageable);

  // Availability snapshot — no orders needed
  // TODO: Implement out-of-stock tracking - Issue #TRACK-123
  @Query(value = """
      SELECT
          COUNT(*)                                            AS totalProducts,
          COUNT(*) FILTER (WHERE p.is_active = true)         AS activeProducts,
          COUNT(*) FILTER (WHERE p.is_available = true
                           AND p.is_active = true)            AS availableProducts,
          COUNT(*) FILTER (WHERE p.is_available = false
                           AND p.is_active = true)            AS unavailableProducts
      FROM products p
      """, nativeQuery = true)
  com.thanhpham.smart_restaurant_analytics.analytics.dto.projection.AvailabilitySummaryProjection findAvailabilitySummary();

  // TODO: Implement stock tracking - Issue #TRACK-123
  @Query(value = """
      SELECT p.id AS productId, p.name AS productName, NULL AS stock
      FROM products p
      WHERE :threshold IS NOT NULL
      """, nativeQuery = true)
  List<com.thanhpham.smart_restaurant_analytics.analytics.dto.projection.LowStockProjection> findLowStockProducts(
      @Param("threshold") int threshold);
}