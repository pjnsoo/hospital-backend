package org.hospital.service.auth;

import lombok.Builder;

import java.time.Duration;

@Builder
public record AuthInfo(
        String accessToken,
        String refreshToken,
        Duration refreshTokenDuration) {
}