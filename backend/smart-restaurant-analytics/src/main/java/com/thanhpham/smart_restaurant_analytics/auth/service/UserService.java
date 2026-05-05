package com.thanhpham.smart_restaurant_analytics.auth.service;

import com.thanhpham.smart_restaurant_analytics.auth.dto.*;
import com.thanhpham.smart_restaurant_analytics.auth.enums.Role;
import com.thanhpham.smart_restaurant_analytics.auth.model.User;
import com.thanhpham.smart_restaurant_analytics.auth.repository.RefreshTokenRepository;
import com.thanhpham.smart_restaurant_analytics.auth.repository.UserRepository;
import com.thanhpham.smart_restaurant_analytics.exception.BusinessRuleException;
import com.thanhpham.smart_restaurant_analytics.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    // ─── READ ─────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Role role, Pageable pageable) {
        if (role != null) {
            return userRepository.findAllByRoleOrderByCreatedAtDesc(role, pageable)
                    .map(UserResponse::from);
        }
        return userRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(UserResponse::from);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        return UserResponse.from(findOrThrow(id));
    }

    // ─── CREATE ───────────────────────────────────────────────────────────────

    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessRuleException(
                    "Username already taken: " + request.getUsername());
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()
                && userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException(
                    "Email already in use: " + request.getEmail());
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .role(request.getRole())
                .enabled(true)
                .accountNonLocked(true)
                .build();

        User saved = userRepository.save(user);
        log.info("User created: id={}, username={}, role={}",
                saved.getId(), saved.getUsername(), saved.getRole());
        return UserResponse.from(saved);
    }

    // ─── UPDATE ───────────────────────────────────────────────────────────────

    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = findOrThrow(id);

        if (request.getEmail() != null && !request.getEmail().isBlank()
                && userRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new BusinessRuleException(
                    "Email already in use: " + request.getEmail());
        }

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());

        User saved = userRepository.save(user);
        log.info("User updated: id={}, role={}", saved.getId(), saved.getRole());
        return UserResponse.from(saved);
    }

    // ─── PASSWORD ─────────────────────────────────────────────────────────────

    public void changePassword(Long id, ChangePasswordRequest request) {
        User user = findOrThrow(id);

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Revoke all sessions — force re-login with new password
        int revoked = refreshTokenRepository.revokeAllByUserId(id);
        log.info("Password changed for userId={}. Revoked {} sessions.", id, revoked);
    }

    // ─── ENABLE / DISABLE ─────────────────────────────────────────────────────

    public void setEnabled(Long id, boolean enabled) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", id);
        }
        userRepository.updateEnabled(id, enabled);

        // If disabling — revoke all active sessions immediately
        if (!enabled) {
            int revoked = refreshTokenRepository.revokeAllByUserId(id);
            log.info("User disabled: id={}. Revoked {} sessions.", id, revoked);
        } else {
            log.info("User enabled: id={}", id);
        }
    }

    // ─── LOCK / UNLOCK ────────────────────────────────────────────────────────

    public void setLocked(Long id, boolean locked) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", id);
        }
        // updateAccountNonLocked(id, !locked) — field is "nonLocked", so invert
        userRepository.updateAccountNonLocked(id, !locked);

        if (locked) {
            int revoked = refreshTokenRepository.revokeAllByUserId(id);
            log.info("User locked: id={}. Revoked {} sessions.", id, revoked);
        } else {
            log.info("User unlocked: id={}", id);
        }
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────

    public void deleteUser(Long id) {
        User user = findOrThrow(id);

        // Cascade on DB handles refresh_tokens deletion (ON DELETE CASCADE)
        userRepository.delete(user);
        log.info("User hard deleted: id={}", id);
    }

    // ─── PRIVATE ──────────────────────────────────────────────────────────────

    private User findOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }
}
