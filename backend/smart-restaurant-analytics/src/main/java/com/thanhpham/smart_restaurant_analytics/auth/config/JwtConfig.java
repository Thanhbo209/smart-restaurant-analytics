package com.thanhpham.smart_restaurant_analytics.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.jwt")
@Getter
@Setter
public class JwtConfig {
    private String secret;
    private long accessTokenExpiry; // seconds
    private long refreshTokenExpiry; // seconds

}