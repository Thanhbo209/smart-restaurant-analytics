package com.thanhpham.smart_restaurant_analytics.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResponse {
    private String accessToken;
    private String refreshToken; // seconds
    private long accessTokenExpiresIn;
}