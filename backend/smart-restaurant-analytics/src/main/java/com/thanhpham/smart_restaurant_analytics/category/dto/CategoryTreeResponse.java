package com.thanhpham.smart_restaurant_analytics.category.dto;

import java.util.List;

import com.thanhpham.smart_restaurant_analytics.category.model.Category;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryTreeResponse {

    private Long id;
    private String name;
    private String slug;
    private String description;
    private Boolean isActive;
    private List<CategoryTreeResponse> children; // recursive

    public static CategoryTreeResponse from(Category category) {
        return CategoryTreeResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .isActive(category.getIsActive())
                // recurse into children — works because children are already loaded
                .children(category.getChildren()
                        .stream()
                        .map(CategoryTreeResponse::from)
                        .toList())
                .build();
    }
}
