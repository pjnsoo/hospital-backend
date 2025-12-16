package org.hospital.util.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Component
public class JwtUtil {

    public static final String CLAIM_TOKEN_TYPE = "token_type";
    public static final String CLAIM_JTI = "jti";

    private final SecretKey key;
    private final String issuer;

    // [핵심] long 대신 Duration 객체로 관리 (계산 실수 방지)
    private final Duration accessTokenDuration;
    private final Duration refreshTokenDuration;

    private final JwtParser jwtParser;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.issuer}") String issuer,
            // yml에서 숫자(분, 일)만 받아옵니다.
            @Value("${jwt.access-token-expire-minutes}") long accessMinutes,
            @Value("${jwt.refresh-token-expire-days}") long refreshDays
    ) {
        this.key = buildKey(secret);
        this.issuer = issuer;

        // [자동 변환] Java Time API를 사용하여 Duration 객체 생성
        this.accessTokenDuration = Duration.ofMinutes(accessMinutes);
        this.refreshTokenDuration = Duration.ofDays(refreshDays);

        this.jwtParser = Jwts.parser().verifyWith(key).build();
    }

    private static SecretKey buildKey(String raw) {
        if (raw == null) raw = "";
        if (raw.length() < 32) {
            raw = (raw + "_jwt_secret_padding_must_be_over_32_bytes_long").substring(0, 48);
        }
        return Keys.hmacShaKeyFor(raw.getBytes(StandardCharsets.UTF_8));
    }

    // ==========================================
    // 토큰 생성 (Duration 사용)
    // ==========================================
    public String createAccessToken(String username) {
        return createToken(username, accessTokenDuration, TokenType.access, null);
    }

    public JwtVo createRefreshToken(String username) {
        String jti = UUID.randomUUID().toString();
        String token = createToken(username, refreshTokenDuration, TokenType.refresh, jti);

        Instant now = Instant.now();
        return JwtVo.builder()
                .token(token)
                .username(username)
                .tokenType(TokenType.refresh)
                .jti(jti)
                .issuedAt(now)
                // [편의성] Instant + Duration 연산 (직관적임)
                .expiration(now.plus(refreshTokenDuration))
                .build();
    }

    private String createToken(String username, Duration duration, TokenType type, String jti) {
        Instant now = Instant.now();
        Instant expiration = now.plus(duration); // 시간 더하기

        var builder = Jwts.builder()
                .issuer(issuer)
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .claim(CLAIM_TOKEN_TYPE, type);

        if (jti != null) {
            builder.claim(CLAIM_JTI, jti);
        }

        return builder.signWith(key).compact();
    }

    // ==========================================
    // 파싱 및 검증
    // ==========================================
    public JwtVo parseToken(String token) {
        try {
            Claims payload = jwtParser.parseSignedClaims(token).getPayload();
            String jti = Objects.toString(payload.get(CLAIM_JTI), null);
            String typeStr = Objects.toString(payload.get(CLAIM_TOKEN_TYPE), null);
            TokenType tokenType = (typeStr != null) ? TokenType.valueOf(typeStr) : null;

            return JwtVo.builder()
                    .token(token)
                    .username(payload.getSubject())
                    .jti(jti)
                    .tokenType(tokenType)
                    .issuedAt(payload.getIssuedAt().toInstant())
                    .expiration(payload.getExpiration().toInstant())
                    .build();
        } catch (Exception e) {
            return null;
        }
    }

    // ★ 컨트롤러에서 쿠키 구울 때 사용 (Duration 객체 리턴)
    public Duration getRefreshTokenDuration() {
        return refreshTokenDuration;
    }
}