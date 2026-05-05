package com.thanhpham.smart_restaurant_analytics.auth.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.thanhpham.smart_restaurant_analytics.auth.enums.Role;
import com.thanhpham.smart_restaurant_analytics.auth.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    Page<User> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<User> findAllByRoleOrderByCreatedAtDesc(Role role, Pageable pageable);

    @Modifying
    @Query("UPDATE User u SET u.enabled = :enabled WHERE u.id = :id")
    int updateEnabled(@Param("id") Long id, @Param("enabled") boolean enabled);

    @Modifying
    @Query("UPDATE User u SET u.accountNonLocked = :locked WHERE u.id = :id")
    int updateAccountNonLocked(@Param("id") Long id, @Param("locked") boolean locked);
}