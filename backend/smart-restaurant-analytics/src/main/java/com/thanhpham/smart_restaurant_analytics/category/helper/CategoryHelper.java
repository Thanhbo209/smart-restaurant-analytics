package com.thanhpham.smart_restaurant_analytics.category.helper;

import org.springframework.stereotype.Component;

import com.thanhpham.smart_restaurant_analytics.category.model.Category;
import com.thanhpham.smart_restaurant_analytics.category.repository.CategoryRepository;
import com.thanhpham.smart_restaurant_analytics.common.utils.SlugUtils;
import com.thanhpham.smart_restaurant_analytics.exception.BusinessRuleException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CategoryHelper {

    private final CategoryRepository categoryRepository;

    public Category resolveParent(Long parentId, Long selfId) {
        if (selfId != null && parentId.equals(selfId)) {
            throw new BusinessRuleException("A category cannot be its own parent.");
        }

        Category parent = categoryRepository.findById(parentId)
                .orElseThrow(() -> new BusinessRuleException(
                        "Parent category not found: id=" + parentId));

        if (!parent.getIsActive()) {
            throw new BusinessRuleException("Parent category is inactive: id=" + parentId);
        }

        // Guard against circular reference: parent must not be a descendant of self
        if (selfId != null && isDescendant(parent, selfId)) {
            throw new BusinessRuleException(
                    "Circular reference: cannot set a descendant as parent.");
        }

        return parent;
    }

    // Walk up the parent chain — if we hit selfId, it's circular
    public boolean isDescendant(Category candidate, Long selfId) {
        Category current = candidate;
        while (current.getParent() != null) {
            if (current.getParent().getId().equals(selfId))
                return true;
            current = current.getParent();
        }
        return false;
    }

    public String generateUniqueSlug(String name, Long excludeId) {
        String base = SlugUtils.toSlug(name);
        String slug = base;
        int suffix = 1;

        while (excludeId == null
                ? categoryRepository.existsBySlug(slug)
                : categoryRepository.existsBySlugAndIdNot(slug, excludeId)) {
            slug = base + "-" + suffix++;
        }
        return slug;
    }
}
