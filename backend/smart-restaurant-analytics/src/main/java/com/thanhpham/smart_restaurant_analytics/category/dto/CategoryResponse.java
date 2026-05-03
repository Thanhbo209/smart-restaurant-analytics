package com.thanhpham.smart_restaurant_analytics.category.dto;

import java.time.LocalDateTime;

import com.thanhpham.smart_restaurant_analytics.category.model.Category;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryResponse {

    private Long id;
    private String name;
    private String slug;
    private String description;
    private Boolean isActive;
    private Long parentId; // null if root
    private String parentName; // null if root — avoids extra lookup on client
    private int childCount; // avoid sending full children list in flat response
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CategoryResponse from(Category category) {
        return from(category, category.getChildren().size());
    }

    public static CategoryResponse from(Category category, int childCount) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .isActive(category.getIsActive())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .parentName(category.getParent() != null ? category.getParent().getName() : null)
                .childCount(childCount)
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}