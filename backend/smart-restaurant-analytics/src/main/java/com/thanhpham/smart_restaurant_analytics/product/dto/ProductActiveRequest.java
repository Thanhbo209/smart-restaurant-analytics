package com.thanhpham.smart_restaurant_analytics.product.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductActiveRequest {

    @NotNull(message = "isActive field is required")
    private Boolean isActive;
}
