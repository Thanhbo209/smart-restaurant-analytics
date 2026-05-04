package com.thanhpham.smart_restaurant_analytics.order.validator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.thanhpham.smart_restaurant_analytics.exception.BusinessRuleException;
import com.thanhpham.smart_restaurant_analytics.order.dto.CreateOrderRequest;
import com.thanhpham.smart_restaurant_analytics.order.enums.OrderType;

@Component
public class OrderValidator {

    public void validateCreateRequest(CreateOrderRequest request) {
        List<String> violations = new ArrayList<>();

        validateTypeConstraints(request, violations);
        validateItems(request, violations);
        validateDiscount(request, violations);

        if (!violations.isEmpty()) {
            throw new BusinessRuleException(String.join("; ", violations));
        }
    }

    private void validateTypeConstraints(CreateOrderRequest request, List<String> violations) {
        OrderType type = request.getType();
        if (type == null) {
            violations.add("Order type is required");
            return;
        }
        switch (type) {
            case DINE_IN -> {
                if (!StringUtils.hasText(request.getTableNumber())) {
                    violations.add("DINE_IN order requires a tableNumber");
                }
                if (StringUtils.hasText(request.getAddress())) {
                    violations.add("DINE_IN order must not have an address");
                }
            }
            case DELIVERY -> {
                if (!StringUtils.hasText(request.getAddress())) {
                    violations.add("DELIVERY order requires an address");
                }
            }
            case TAKEAWAY -> {
                if (StringUtils.hasText(request.getTableNumber())) {
                    violations.add("TAKEAWAY order must not have a tableNumber");
                }
                if (StringUtils.hasText(request.getAddress())) {
                    violations.add("TAKEAWAY order must not have an address");
                }
            }
        }
    }

    private void validateItems(CreateOrderRequest request, List<String> violations) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            violations.add("Order must contain at least one item");
        }
    }

    private void validateDiscount(CreateOrderRequest request, List<String> violations) {
        BigDecimal discount = request.getDiscountAmount();
        if (discount != null && discount.compareTo(BigDecimal.ZERO) < 0) {
            violations.add("Discount amount cannot be negative");
        }
    }
}
