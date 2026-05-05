package com.thanhpham.smart_restaurant_analytics.auth.dto;

import com.thanhpham.smart_restaurant_analytics.auth.enums.Role;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private String accessToken;
    private String refreshToken; // raw token — client stores this securely
    private long accessTokenExpiresIn; // seconds
    private Long userId;
    private String username;
    private String fullName;
    private Role role;
}