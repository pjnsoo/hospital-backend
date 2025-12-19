package org.hospital.model.table;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Instant;


@Data
@Builder
@Accessors(fluent = true)
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