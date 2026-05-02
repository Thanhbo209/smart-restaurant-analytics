package com.thanhpham.smart_restaurant_analytics.product.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thanhpham.smart_restaurant_analytics.product.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Paginated list of active products
    @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE p.isActive = true")
    Page<Product> findAllByIsActiveTrue(Pageable pageable);

    @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE p.id = :id")
    Optional<Product> findByIdWithCategory(@Param("id") Long id);

    // Name search — case-insensitive
    Page<Product> findByNameContainingIgnoreCaseAndIsActiveTrue(String name, Pageable pageable);
}
