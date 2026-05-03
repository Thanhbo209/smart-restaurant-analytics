package com.thanhpham.smart_restaurant_analytics.product.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thanhpham.smart_restaurant_analytics.product.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Paginated list of active products
    // ---LIST
    @EntityGraph(attributePaths = { "category" })
    Page<Product> findAllByIsActiveTrue(Pageable pageable);

    @Query("""
            SELECT p FROM Product p JOIN FETCH p.category
            WHERE p.isActive = true
            AND (:categoryId IS NULL OR p.category.id = :categoryId)
            AND (:minPrice IS NULL OR p.price >= :minPrice)
            AND (:maxPrice IS NULL OR p.price <= :maxPrice)
            AND (:isAvailable IS NULL OR p.isAvailable = :isAvailable)
            """)
    Page<Product> findAllWithFilters(
            @Param("categoryId") Long categoryId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("isAvailable") Boolean isAvailable,
            Pageable pageable);

    // ---Name search — case-insensitive
    @EntityGraph(attributePaths = { "category" })
    Page<Product> findByNameContainingIgnoreCaseAndIsActiveTrue(String name, Pageable pageable);

    // ---SINGLE FETCH

    @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE p.slug = :slug AND p.isActive = true")
    Optional<Product> findBySlugWithCategory(@Param("slug") String slug);

    @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE p.sku = :sku AND p.isActive = true")
    Optional<Product> findBySkuWithCategory(@Param("sku") String sku);

    @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE p.id = :id")
    Optional<Product> findByIdWithCategory(@Param("id") Long id);

    // ---UNIQUENESS CHECKS

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, Long id);

    // ---PATCH OPERATIONS (targeted UPDATE — no full entity load)

    @Modifying
    @Query("UPDATE Product p SET p.isAvailable = :isAvailable WHERE p.id = :id")
    int updateAvailability(@Param("id") Long id, @Param("isAvailable") Boolean isAvailable);

    @Modifying
    @Query("UPDATE Product p SET p.isActive = :isActive WHERE p.id = :id")
    int updateActive(@Param("id") Long id, @Param("isActive") Boolean isActive);

}
