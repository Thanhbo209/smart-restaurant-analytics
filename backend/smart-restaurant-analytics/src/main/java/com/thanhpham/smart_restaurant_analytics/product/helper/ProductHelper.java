package com.thanhpham.smart_restaurant_analytics.product.helper;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.thanhpham.smart_restaurant_analytics.category.model.Category;
import com.thanhpham.smart_restaurant_analytics.category.repository.CategoryRepository;
import com.thanhpham.smart_restaurant_analytics.common.utils.SlugUtils;
import com.thanhpham.smart_restaurant_analytics.exception.BusinessRuleException;
import com.thanhpham.smart_restaurant_analytics.exception.ResourceNotFoundException;
import com.thanhpham.smart_restaurant_analytics.product.dto.ProductRequest;
import com.thanhpham.smart_restaurant_analytics.product.model.Product;
import com.thanhpham.smart_restaurant_analytics.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ProductHelper {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public Category resolveCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .filter(c -> c.getIsActive())
                .orElseThrow(() -> new BusinessRuleException(
                        "Category not found or inactive: id=" + categoryId));
    }

    public String generateUniqueSlug(String name, Long excludeId) {
        String base = SlugUtils.toSlug(name);
        String slug = base;
        int suffix = 1;

        while (excludeId == null
                ? productRepository.existsBySlug(slug)
                : productRepository.existsBySlugAndIdNot(slug, excludeId)) {
            slug = base + "-" + suffix++;
        }
        return slug;
    }

    public void validateSkuUniqueness(String sku, Long excludeId) {
        if (!StringUtils.hasText(sku))
            return;

        boolean conflict = excludeId == null
                ? productRepository.existsBySku(sku)
                : productRepository.existsBySkuAndIdNot(sku, excludeId);

        if (conflict)
            throw new BusinessRuleException("SKU already in use: " + sku);
    }

    public void ensureProductExists(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product", id);
        }
    }

    public void mapRequestToEntity(ProductRequest request, Product product,
            Category category, String slug, boolean isNew) {

        product.setName(request.getName());
        product.setSlug(slug);
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCost(request.getCost());
        product.setSku(request.getSku());
        product.setImageUrl(request.getImageUrl());
        product.setImagePublicId(request.getImagePublicId());
        product.setCategory(category);
        if (isNew) {
            product.setIsActive(true);
            product.setIsAvailable(true);
        }
    }
}
