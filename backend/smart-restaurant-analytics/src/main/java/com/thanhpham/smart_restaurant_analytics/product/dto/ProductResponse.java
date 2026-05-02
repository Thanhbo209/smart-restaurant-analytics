package com.thanhpham.smart_restaurant_analytics.product.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.thanhpham.smart_restaurant_analytics.category.dto.CategoryResponse;
import com.thanhpham.smart_restaurant_analytics.product.model.Product;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductResponse {

    private Long id;
    private String name;
    private String slug;
    private String sku;
    private String description;
    private BigDecimal price;
    private BigDecimal cost; // present in response — admin hides it in UI if needed
    private Integer stock;
    private String imageUrl;
    private Boolean isActive;
    private Boolean isAvailable;
    private CategoryResponse category; // embedded — avoids extra round-trip for category name
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProductResponse from(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .sku(product.getSku())
                .description(product.getDescription())
                .price(product.getPrice())
                .cost(product.getCost())
                .stock(product.getStock())
                .imageUrl(product.getImageUrl())
                .isActive(product.getIsActive())
                .isAvailable(product.getIsAvailable())
                // category is LAZY — only safe to call here if the caller
                // used @EntityGraph or join fetch in the repository
                .category(CategoryResponse.from(product.getCategory()))
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
