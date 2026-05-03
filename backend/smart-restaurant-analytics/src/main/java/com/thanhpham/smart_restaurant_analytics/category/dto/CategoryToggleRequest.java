package com.thanhpham.smart_restaurant_analytics.category.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryToggleRequest {

    @NotNull(message = "isActive field is required")
    private Boolean isActive;
}
