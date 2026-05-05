package com.thanhpham.smart_restaurant_analytics.auth.controller;

import com.thanhpham.smart_restaurant_analytics.auth.dto.*;
import com.thanhpham.smart_restaurant_analytics.auth.model.User;
import com.thanhpham.smart_restaurant_analytics.auth.repository.UserRepository;
import com.thanhpham.smart_restaurant_analytics.auth.service.AuthService;
import com.thanhpham.smart_restaurant_analytics.common.ApiResponse;
import com.thanhpham.smart_restaurant_analytics.exception.ResourceNotFoundException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.login(request)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(
            @RequestBody @Valid RefreshRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.refresh(request)));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestBody @Valid RefreshRequest request) {
        authService.logout(request.getRefreshToken(), request.getDeviceId());
        return ResponseEntity.ok(ApiResponse.success(null, "Logged out successfully"));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MeResponse>> me(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User", "username", userDetails.getUsername()));

        return ResponseEntity.ok(ApiResponse.success(MeResponse.from(user)));
    }
}