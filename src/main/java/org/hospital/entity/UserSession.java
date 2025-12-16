package org.hospital.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSession {
    long userNo;
    String jti;
    String platform;
    String deviceId;
    String pushToken;
    Instant issuedAt;
    Instant expiresAt;
    boolean revoked;
    String userAgent;
    String ip;
    Instant createdAt;
    Instant updatedAt;
}