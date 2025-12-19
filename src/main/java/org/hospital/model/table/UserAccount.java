package org.hospital.model.table;

import lombok.Builder;
import org.hospital.model.enums.UserRole;
import org.hospital.model.enums.UserStatus;

import java.time.Instant;

@Builder
public record UserAccount(
        long userNo,
        String username,
        String password,
        UserRole role,
        UserStatus status,
        Instant pwChangedAt,
        Instant lastAccessAt,
        Instant createdAt,
        Instant updatedAt) {
}