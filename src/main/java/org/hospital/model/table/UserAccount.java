package org.hospital.model.table;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hospital.model.enums.UserRole;
import org.hospital.model.enums.UserStatus;

import java.time.Instant;

@Data
@Builder
@Accessors(fluent = true)
public class UserAccount {
    long userNo;
    String username;
    String password;
    UserRole role;
    UserStatus status;
    Instant pwChangedAt;
    Instant lastAccessAt;
    Instant createdAt;
    Instant updatedAt;
}