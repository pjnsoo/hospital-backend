package org.hospital.component.jwt;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Duration;
import java.time.Instant;

@Data
@Builder
@Accessors(fluent = true)
public class RefreshToken {
    private String token;
    private String username;
    private TokenType tokenType;
    private String jti;
    private Instant issuedAt;
    private Instant expiration;
    private Duration duration;


    public boolean isValid() {

        if (username == null || tokenType == null || expiration == null) {
            return false;
        }

        if (tokenType == TokenType.refresh && jti == null) {
            return false;
        }

        // 현재 시간과 비교
        return Instant.now().isBefore(expiration);
    }
}
