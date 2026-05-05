package com.thanhpham.smart_restaurant_analytics.auth.service;

import com.thanhpham.smart_restaurant_analytics.auth.config.JwtConfig;
import com.thanhpham.smart_restaurant_analytics.auth.dto.*;
import com.thanhpham.smart_restaurant_analytics.auth.model.*;
import com.thanhpham.smart_restaurant_analytics.auth.repository.*;
import com.thanhpham.smart_restaurant_analytics.exception.BusinessRuleException;
import com.thanhpham.smart_restaurant_analytics.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AuthService {

        private final AuthenticationManager authenticationManager;
        private final UserRepository userRepository;
        private final RefreshTokenRepository refreshTokenRepository;
        private final JwtService jwtService;
        private final JwtConfig jwtConfig;
        private final PasswordEncoder passwordEncoder;

        // ─── LOGIN ────────────────────────────────────────────────────────────────

        public LoginResponse login(LoginRequest request) {
                try {
                        authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(
                                                        request.getUsername(), request.getPassword()));
                } catch (AuthenticationException ex) {
                        // Deliberate: same message for wrong username AND wrong password
                        // Prevents username enumeration attacks
                        throw new BusinessRuleException("Invalid credentials");
                }

                User user = userRepository.findByUsername(request.getUsername())
                                .orElseThrow(() -> new ResourceNotFoundException("User", "username",
                                                request.getUsername()));

                if (!user.isEnabled()) {
                        throw new BusinessRuleException("Account is suspended. Contact your administrator.");
                }

                String accessToken = jwtService.generateAccessToken(user);
                String rawRefresh = jwtService.generateRawRefreshToken();
                String hashedRefresh = jwtService.hashToken(rawRefresh);

                // Revoke existing session for same device — one device = one session
                refreshTokenRepository.revokeActiveByUserIdAndDevice(user.getId(), request.getDeviceId());

                RefreshToken refreshToken = RefreshToken.builder()
                                .user(user)
                                .tokenHash(hashedRefresh)
                                .deviceId(request.getDeviceId())
                                .expiresAt(LocalDateTime.now().plusSeconds(jwtConfig.getRefreshTokenExpiry()))
                                .revoked(false)
                                .build();
                refreshTokenRepository.save(refreshToken);

                log.info("User logged in: username={}, role={}, deviceId={}",
                                user.getUsername(), user.getRole(), request.getDeviceId());

                System.out.println(
                                new BCryptPasswordEncoder().matches(
                                                "password",
                                                "$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi."));

                return LoginResponse.builder()
                                .accessToken(accessToken)
                                .refreshToken(rawRefresh) // raw — client never sees the hash
                                .accessTokenExpiresIn(jwtConfig.getAccessTokenExpiry())
                                .userId(user.getId())
                                .username(user.getUsername())
                                .fullName(user.getFullName())
                                .role(user.getRole())
                                .build();
        }

        // ─── REFRESH ──────────────────────────────────────────────────────────────

        public TokenResponse refresh(RefreshRequest request) {
                String hash = jwtService.hashToken(request.getRefreshToken());

                RefreshToken stored = refreshTokenRepository.findByTokenHash(hash)
                                .orElseThrow(() -> new BusinessRuleException("Invalid refresh token"));

                if (!stored.isValid()) {
                        // Token reuse detected — revoke ALL sessions for this user
                        // Indicates potential token theft — aggressive response
                        if (stored.isRevoked()) {
                                log.warn("Refresh token reuse detected for userId={}. Revoking all sessions.",
                                                stored.getUser().getId());
                                refreshTokenRepository.revokeAllByUserId(stored.getUser().getId());
                        }
                        throw new BusinessRuleException("Refresh token is expired or revoked");
                }

                if (!stored.getDeviceId().equals(request.getDeviceId())) {
                        // Token used from different device — revoke it
                        stored.setRevoked(true);
                        log.warn("Refresh token deviceId mismatch for userId={}", stored.getUser().getId());
                        throw new BusinessRuleException("Invalid refresh token");
                }

                User user = stored.getUser();

                // Rotate — invalidate old, issue new
                stored.setRevoked(true);

                String newRawRefresh = jwtService.generateRawRefreshToken();
                String newHashedRefresh = jwtService.hashToken(newRawRefresh);

                RefreshToken newToken = RefreshToken.builder()
                                .user(user)
                                .tokenHash(newHashedRefresh)
                                .deviceId(request.getDeviceId())
                                .expiresAt(LocalDateTime.now().plusSeconds(jwtConfig.getRefreshTokenExpiry()))
                                .revoked(false)
                                .build();
                refreshTokenRepository.save(newToken);

                String newAccessToken = jwtService.generateAccessToken(user);

                log.info("Token refreshed: userId={}, deviceId={}", user.getId(), request.getDeviceId());

                return TokenResponse.builder()
                                .accessToken(newAccessToken)
                                .refreshToken(newRawRefresh)
                                .accessTokenExpiresIn(jwtConfig.getAccessTokenExpiry())
                                .build();
        }

        // ─── LOGOUT ───────────────────────────────────────────────────────────────

        public void logout(String rawRefreshToken, String deviceId) {
                String hash = jwtService.hashToken(rawRefreshToken);
                refreshTokenRepository.findByTokenHash(hash)
                                .ifPresent(t -> t.setRevoked(true));
                log.info("User logged out: deviceId={}", deviceId);
        }
}