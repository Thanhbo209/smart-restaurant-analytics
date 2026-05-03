package com.thanhpham.smart_restaurant_analytics.product.service;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thanhpham.smart_restaurant_analytics.category.model.Category;
import com.thanhpham.smart_restaurant_analytics.exception.ResourceNotFoundException;
import com.thanhpham.smart_restaurant_analytics.product.dto.ProductActiveRequest;
import com.thanhpham.smart_restaurant_analytics.product.dto.ProductAvailabilityRequest;
import com.thanhpham.smart_restaurant_analytics.product.dto.ProductRequest;
import com.thanhpham.smart_restaurant_analytics.product.dto.ProductResponse;
import com.thanhpham.smart_restaurant_analytics.product.helper.ProductHelper;
import com.thanhpham.smart_restaurant_analytics.product.model.Product;
import com.thanhpham.smart_restaurant_analytics.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;

    private final ProductHelper helper;

    // GET all products
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAllByIsActiveTrue(pageable)
                .map(ProductResponse::from);
    }

    // GET products with filters
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProductsWithFilters(
            Long categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean isAvailable,
            Pageable pageable) {
        return productRepository.findAllWithFilters(categoryId, minPrice, maxPrice, isAvailable, pageable)
                .map(ProductResponse::from);
    }

    // GET product by id
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        return productRepository.findByIdWithCategory(id)
                .map(ProductResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }

    // GET product by slug
    @Transactional(readOnly = true)
    public ProductResponse getProductBySlug(String slug) {
        return productRepository.findBySlugWithCategory(slug)
                .map(ProductResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "slug", slug));
    }

    // GET product by sku
    @Transactional(readOnly = true)
    public ProductResponse getProductBySku(String sku) {
        return productRepository.findBySkuWithCategory(sku)
                .map(ProductResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "sku", sku));
    }

    // Search
    @Transactional(readOnly = true)
    public Page<ProductResponse> search(String name, Pageable pageable) {
        return productRepository
                .findByNameContainingIgnoreCaseAndIsActiveTrue(name, pageable)
                .map(ProductResponse::from);
    }

    // POST create product
    public ProductResponse createProduct(ProductRequest req) {
        Category category = helper.resolveCategory(req.getCategoryId());

        String slug = helper.generateUniqueSlug(req.getName(), null);
        helper.validateSkuUniqueness(req.getSku(), null);

        Product product = new Product();
        helper.mapRequestToEntity(req, product, category, slug, true);

        Product savedProduct = productRepository.save(product);
        log.info("Product created: id={}, slug={}", savedProduct.getId(), savedProduct.getSlug());
        return ProductResponse.from(savedProduct);
    }

    // PUT update product
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        Category category = helper.resolveCategory(request.getCategoryId());

        String slug = helper.generateUniqueSlug(request.getName(), id);
        helper.validateSkuUniqueness(request.getSku(), id);

        helper.mapRequestToEntity(request, product, category, slug, false);

        Product saved = productRepository.save(product);
        log.info("Product updated: id={}", saved.getId());
        return ProductResponse.from(saved);
    }

    // PATCH products
    public void updateAvailability(Long id, ProductAvailabilityRequest request) {
        helper.ensureProductExists(id);
        int updated = productRepository.updateAvailability(id, request.getIsAvailable());
        if (updated == 0)
            throw new ResourceNotFoundException("Product", id);
        log.info("Product availability updated: id={}, isAvailable={}", id, request.getIsAvailable());
    }

    public void updateActive(Long id, ProductActiveRequest request) {
        helper.ensureProductExists(id);
        int updated = productRepository.updateActive(id, request.getIsActive());
        if (updated == 0)
            throw new ResourceNotFoundException("Product", id);
        log.info("Product active status updated: id={}, isActive={}", id, request.getIsActive());
    }

    // DELETE products
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        productRepository.delete(product);
        log.info("Product deleted: id={}", id);
    }

}
