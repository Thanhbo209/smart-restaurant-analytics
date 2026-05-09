package com.thanhpham.smart_restaurant_analytics.analytics.dto.projection;

import java.math.BigDecimal;

public interface MarginProjection {
    Long getProductId();

    String getProductName();

    String getCategoryName();

    BigDecimal getPrice();

    BigDecimal getCost();

    Double getMarginPercent();

    BigDecimal getMarginAmount();

    Boolean getIsAvailable();
}