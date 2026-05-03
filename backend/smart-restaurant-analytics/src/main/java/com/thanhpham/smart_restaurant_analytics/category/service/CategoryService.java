package com.thanhpham.smart_restaurant_analytics.category.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thanhpham.smart_restaurant_analytics.category.dto.CategoryRequest;
import com.thanhpham.smart_restaurant_analytics.category.dto.CategoryResponse;
import com.thanhpham.smart_restaurant_analytics.category.dto.CategoryToggleRequest;
import com.thanhpham.smart_restaurant_analytics.category.dto.CategoryTreeResponse;
import com.thanhpham.smart_restaurant_analytics.category.helper.CategoryHelper;
import com.thanhpham.smart_restaurant_analytics.category.model.Category;
import com.thanhpham.smart_restaurant_analytics.category.repository.CategoryRepository;
import com.thanhpham.smart_restaurant_analytics.exception.BusinessRuleException;
import com.thanhpham.smart_restaurant_analytics.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryHelper helper;

    // GET categories tree
    @Transactional(readOnly = true)
    public List<CategoryTreeResponse> getCategoryTree() {
        return categoryRepository.findRootCategoriesWithChildren()
                .stream()
                .map(CategoryTreeResponse::from)
                .toList();
    }

    // GET all flats categories
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllFlat(boolean rootOnly) {
        List<Category> categories = rootOnly
                ? categoryRepository.findAllByParentIsNullAndIsActiveTrueOrderByNameAsc()
                : categoryRepository.findAllByIsActiveTrueOrderByNameAsc();
        return categories.stream().map(CategoryResponse::from).toList();
    }

    // GET category by id
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(CategoryResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
    }

    // POST categories
    public CategoryResponse createCategory(CategoryRequest request) {
        String slug = helper.generateUniqueSlug(request.getName(), null);

        Category category = new Category();
        category.setName(request.getName());
        category.setSlug(slug);
        category.setDescription(request.getDescription());
        category.setIsActive(true);

        if (request.getParentId() != null) {
            Category parent = helper.resolveParent(request.getParentId(), null);
            category.setParent(parent);
        }

        Category savedCategory = categoryRepository.save(category);
        log.info("Category created: id={}, slug={}", savedCategory.getId(), savedCategory.getSlug());
        return CategoryResponse.from(savedCategory);
    }

    // PUT categories
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));

        String slug = helper.generateUniqueSlug(request.getName(), id);
        category.setName(request.getName());
        category.setSlug(slug);
        category.setDescription(request.getDescription());

        if (request.getParentId() != null) {
            Category parent = helper.resolveParent(request.getParentId(), id);
            category.setParent(parent);
        } else {
            category.setParent(null); // promote to root
        }

        Category saved = categoryRepository.save(category);
        log.info("Category updated: id={}", saved.getId());
        return CategoryResponse.from(saved);
    }

    // PATCH toggle
    public void toggleActive(Long id, CategoryToggleRequest request) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category", id);
        }
        int updated = categoryRepository.updateActive(id, request.getIsActive());
        if (updated == 0)
            throw new ResourceNotFoundException("Category", id);
        log.info("Category toggled: id={}, isActive={}", id, request.getIsActive());
    }

    // DETELE categories
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));

        // 1. Check children
        boolean hasChildren = categoryRepository.existsByParentId(id);
        if (hasChildren) {
            throw new BusinessRuleException(
                    "Cannot delete category with subcategories. Delete or move them first.");
        }

        // 2. Check products
        long productCount = categoryRepository.countActiveProductsByCategoryId(id);
        if (productCount > 0) {
            throw new BusinessRuleException(
                    "Cannot delete category with " + productCount + " active product(s). Deactivate them first.");
        }

        categoryRepository.delete(category);
        log.info("Category hard deleted: id={}", id);
    }
}
