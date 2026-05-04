package com.thanhpham.smart_restaurant_analytics.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.thanhpham.smart_restaurant_analytics.order.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long>,
        JpaSpecificationExecutor<Order> {
    // JpaSpecificationExecutor gives us:
    // Page<Order> findAll(Specification<Order> spec, Pageable pageable)
    // No JPQL needed for filtering.
}