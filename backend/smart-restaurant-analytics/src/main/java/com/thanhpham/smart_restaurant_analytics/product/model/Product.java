package com.thanhpham.smart_restaurant_analytics.product.model;

import java.math.BigDecimal;

import com.thanhpham.smart_restaurant_analytics.category.model.Category;
import com.thanhpham.smart_restaurant_analytics.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products") // never rely on Hibernate's name guessing
@Getter
@Setter
@NoArgsConstructor
public class Product extends BaseEntity {

    // id, createdAt, updatedAt already inherited from BaseEntity

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 200)
    private String slug;

    @NotNull
    @Positive
    private BigDecimal price;

    @PositiveOrZero
    private BigDecimal cost; // purchase cost use for margin analytics, temporary truth

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100, unique = true)
    private String sku; // stock keeping unit

    @Column(length = 500)
    private String imageUrl;

    @Column(length = 200)
    private String imagePublicId;

    // Existed in system
    @Column(nullable = false)
    private Boolean isActive = true;

    // On/Off for products
    @Column(nullable = false)
    private Boolean isAvailable = true;

    // RELATION
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id")
    private Category category;
}
