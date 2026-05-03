package com.thanhpham.smart_restaurant_analytics.category.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.thanhpham.smart_restaurant_analytics.category.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    // ---TREE

    // Fetch only root categories WITH their children eagerly in one query.
    // The children's children are fetched via the recursive
    // CategoryTreeResponse.from()
    // — works because the whole graph is loaded in this session.
    @Query("""
            SELECT DISTINCT c FROM Category c
            LEFT JOIN FETCH c.children ch
            LEFT JOIN FETCH ch.children
            WHERE c.parent IS NULL
            AND c.isActive = true
            ORDER BY c.name ASC
            """)
    List<Category> findRootCategoriesWithChildren();

    // ---FLAT LIST

    List<Category> findAllByIsActiveTrueOrderByNameAsc();

    List<Category> findAllByParentIsNullAndIsActiveTrueOrderByNameAsc();

    // ---SINGLE FETCH

    Optional<Category> findBySlug(String slug);

    // ---UNIQUENESS CHECKS

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);

    boolean existsByParentId(Long parentId);
    // ---PATCH

    @Modifying
    @Query("UPDATE Category c SET c.isActive = :isActive WHERE c.id = :id")
    int updateActive(@Param("id") Long id, @Param("isActive") Boolean isActive);

    // ---SAFETY CHECK before delete

    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId AND p.isActive = true")
    long countActiveProductsByCategoryId(@Param("categoryId") Long categoryId);

}
