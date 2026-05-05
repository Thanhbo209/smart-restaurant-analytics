package com.thanhpham.smart_restaurant_analytics.auth.dto;

import com.thanhpham.smart_restaurant_analytics.auth.enums.Role;
import com.thanhpham.smart_restaurant_analytics.auth.model.User;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MeResponse {
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private Role role;

    public static MeResponse from(User user) {
        return MeResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
