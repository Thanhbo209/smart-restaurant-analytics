package com.thanhpham.smart_restaurant_analytics.auth.service;

import com.thanhpham.smart_restaurant_analytics.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenCleanupJob {

    private final RefreshTokenRepository refreshTokenRepository;

    // Runs at 3:00 AM every day
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void purgeExpiredTokens() {
        LocalDateTime cutoff = LocalDateTime.now();
        int deleted = refreshTokenRepository.deleteExpiredBefore(cutoff);
        log.info("Purged {} expired refresh tokens", deleted);
    }
}
