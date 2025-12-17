package org.hospital.model.table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAccount {
    long userNo;
    String username;
    String password;
    String nickname;
    String role;
    String status;
    Instant createdAt;
    Instant updatedAt;
}