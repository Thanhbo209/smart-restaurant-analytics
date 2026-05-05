package com.thanhpham.smart_restaurant_analytics.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshRequest {

    @NotBlank
    private String refreshToken;

    @NotBlank
    private String deviceId;
}
