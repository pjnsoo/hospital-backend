package org.hospital.util.jwt;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class JwtVo {
    private String token;
    private String username;
    private TokenType tokenType;
    private String jti;
    private Instant issuedAt;
    private Instant expiration;


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
