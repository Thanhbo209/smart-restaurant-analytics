package com.thanhpham.smart_restaurant_analytics.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    // Client-generated UUID — identifies the device/session
    @NotBlank(message = "Device ID is required")
    private String deviceId;
}