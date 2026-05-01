package com.thanhpham.smart_restaurant_analytics.category.model;

import java.util.ArrayList;
import java.util.List;

import com.thanhpham.smart_restaurant_analytics.common.BaseEntity;
import com.thanhpham.smart_restaurant_analytics.product.model.Product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "categories")
@Getter
@Setter
public class Category extends BaseEntity {
    // id, createdAt, updatedAt already inherited from BaseEntity

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 150, unique = true)
    private String slug; // URL / filter / AI mapping

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Boolean isActive = true;

    // self-reference, support hierarchy (Food > Drink > Milk Tea)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = true)
    private Category parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<Category> children = new ArrayList<>();

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();
}
