package com.thanhpham.smart_restaurant_analytics.product.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thanhpham.smart_restaurant_analytics.exception.ResourceNotFoundException;
import com.thanhpham.smart_restaurant_analytics.product.dto.ProductResponse;
import com.thanhpham.smart_restaurant_analytics.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    // GET all products
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAllByIsActiveTrue(pageable)
                .map(ProductResponse::from);
    }

    // GET product by id
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        return productRepository.findByIdWithCategory(id)
                .map(ProductResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }

    // Search
    @Transactional(readOnly = true)
    public Page<ProductResponse> search(String name, Pageable pageable) {
        return productRepository
                .findByNameContainingIgnoreCaseAndIsActiveTrue(name, pageable)
                .map(ProductResponse::from);
    }
}
