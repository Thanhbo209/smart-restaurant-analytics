package com.thanhpham.smart_restaurant_analytics.common.utils;

import java.text.Normalizer;

public class SlugUtils {
    public static String toSlug(String input) {
        if (input == null || input.isEmpty())
            return "";

        // Remove accents (Vietnamese safe)
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        // Lowercase
        String slug = normalized.toLowerCase();

        // Replace non-alphanumeric with hyphen
        slug = slug.replaceAll("[^a-z0-9]+", "-");

        // Trim hyphens
        slug = slug.replaceAll("^-+|-+$", "");

        return slug;
    }
}
