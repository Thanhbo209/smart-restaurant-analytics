package com.thanhpham.smart_restaurant_analytics.category.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thanhpham.smart_restaurant_analytics.category.dto.CategoryRequest;
import com.thanhpham.smart_restaurant_analytics.category.dto.CategoryResponse;
import com.thanhpham.smart_restaurant_analytics.category.dto.CategoryToggleRequest;
import com.thanhpham.smart_restaurant_analytics.category.dto.CategoryTreeResponse;
import com.thanhpham.smart_restaurant_analytics.category.service.CategoryService;
import com.thanhpham.smart_restaurant_analytics.common.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Validated
public class CategoryController {

    private final CategoryService categoryService;

    // GET tree
    // ---------------------------------------------------------------------

    @GetMapping("/tree")
    public ResponseEntity<ApiResponse<List<CategoryTreeResponse>>> getTree() {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getCategoryTree()));
    }

    // GET flat list
    // ---------------------------------------------------------------------

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAll(
            @RequestParam(defaultValue = "false") boolean rootOnly) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getAllFlat(rootOnly)));
    }

    // GET by id
    // ---------------------------------------------------------------------

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getCategoryById(id)));
    }

    // CREATE ---------------------------------------------------------------------

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> create(
            @RequestBody @Valid CategoryRequest request) {
        CategoryResponse response = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Category created successfully"));
    }

    // UPDATE ---------------------------------------------------------------------

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(
            @PathVariable Long id,
            @RequestBody @Valid CategoryRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(categoryService.updateCategory(id, request), "Category updated successfully"));
    }

    // PATCH toggle
    // ---------------------------------------------------------------------

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<Void>> toggle(
            @PathVariable Long id,
            @RequestBody @Valid CategoryToggleRequest request) {
        categoryService.toggleActive(id, request);
        return ResponseEntity.ok(ApiResponse.success(null,
                "Category " + (request.getIsActive() ? "enabled" : "disabled") + " successfully"));
    }

    // DELETE ---------------------------------------------------------------------

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Category deleted successfully"));
    }
}