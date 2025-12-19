package org.hospital.model.table;

import lombok.Builder;

import java.time.Instant;


@Builder
public record UserSession(
        long userNo,
        String jti,
        String platform,
        String deviceId,
        String pushToken,
        Instant issuedAt,
        Instant expiresAt,
        boolean revoked,
        String userAgent,
        String ip,
        Instant createdAt,
        Instant updatedAt) {
}