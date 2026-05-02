package com.thanhpham.smart_restaurant_analytics.product.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(max = 200, message = "Name must not exceed 200 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Price must have at most 2 decimal places")
    private BigDecimal price;

    // optional — used for margin analytics
    @DecimalMin(value = "0.00", inclusive = true, message = "Cost cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Cost must have at most 2 decimal places")
    private BigDecimal cost;

    @Size(max = 100, message = "SKU must not exceed 100 characters")
    private String sku;

    private String imageUrl;
    private String imagePublicId;

    @NotNull(message = "Category is required")
    @Positive(message = "Category ID must be a positive number")
    private Long categoryId;
}