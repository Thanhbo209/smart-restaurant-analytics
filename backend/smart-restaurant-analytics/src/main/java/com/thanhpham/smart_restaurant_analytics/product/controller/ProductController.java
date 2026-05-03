package com.thanhpham.smart_restaurant_analytics.product.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thanhpham.smart_restaurant_analytics.common.ApiResponse;
import com.thanhpham.smart_restaurant_analytics.product.dto.ProductActiveRequest;
import com.thanhpham.smart_restaurant_analytics.product.dto.ProductAvailabilityRequest;
import com.thanhpham.smart_restaurant_analytics.product.dto.ProductRequest;
import com.thanhpham.smart_restaurant_analytics.product.dto.ProductResponse;
import com.thanhpham.smart_restaurant_analytics.product.service.ProductService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@Validated
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor // DI
public class ProductController {
    private final ProductService productService;

    // --- GET all products ---------------------------------------------------
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getAllProducts(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") @Pattern(regexp = "(?i)^(asc|desc)$", message = "direction must be asc or desc") String direction,
            @RequestParam(required = false) String search) {

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductResponse> result = (search != null && !search.isBlank())
                ? productService.search(search, pageable)
                : productService.getAllProducts(pageable);

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // --- GET by id -----------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(productService.getProductById(id)));
    }

    // --- GET by Slug -----------------------------------------------------
    @GetMapping("by-slug/{slug}")
    public ResponseEntity<ApiResponse<ProductResponse>> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.success(productService.getProductBySlug(slug)));
    }

    // --- GET by Sku -----------------------------------------------------
    @GetMapping("/by-sku/{sku}")
    public ResponseEntity<ApiResponse<ProductResponse>> getBySku(@PathVariable String sku) {
        return ResponseEntity.ok(ApiResponse.success(productService.getProductBySku(sku)));
    }

    // --- POST products -----------------------------------------------------
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> create(
            @RequestBody @Valid ProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Product created successfully"));
    }

    // ---UPDATE (full fields) -----------------------------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> update(
            @PathVariable Long id,
            @RequestBody @Valid ProductRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(productService.updateProduct(id, request), "Product updated successfully"));
    }

    // --- PATCH availability -----------------------------------------------------

    @PatchMapping("/{id}/availability")
    public ResponseEntity<ApiResponse<Void>> updateAvailability(
            @PathVariable Long id,
            @RequestBody @Valid ProductAvailabilityRequest request) {
        productService.updateAvailability(id, request);
        return ResponseEntity.ok(ApiResponse.success(null,
                "Product availability updated to: " + request.getIsAvailable()));
    }

    // --- PATCH active (soft delete / restore) ------------------------------
    @PatchMapping("/{id}/active")
    public ResponseEntity<ApiResponse<Void>> updateActive(
            @PathVariable Long id,
            @RequestBody @Valid ProductActiveRequest request) {
        productService.updateActive(id, request);
        return ResponseEntity.ok(ApiResponse.success(null,
                "Product active status updated to: " + request.getIsActive()));
    }

    // --- DELETE (hard) -----------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Product deleted successfully"));
    }

}
