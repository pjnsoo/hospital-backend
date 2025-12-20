package org.hospital.service.auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hospital.component.jwt.JwtUtil;
import org.hospital.component.jwt.RefreshToken;
import org.hospital.component.jwt.TokenType;
import org.hospital.model.mapper.UserAccountMapper;
import org.hospital.model.mapper.UserSessionMapper;
import org.hospital.model.table.UserAccount;
import org.hospital.model.table.UserSession;
import org.hospital.service.DefaultHeader;
import org.hospital.service.ResStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final UserSessionMapper userSessionMapper;
    private final UserAccountMapper userAccountMapper;
    private final PasswordEncoder passwordEncoder;

    public AuthResult login(boolean isSecure, DefaultHeader header, LoginRequest request) {
        // 1. 사용자 조회 및 비밀번호 검증 (동일)
        UserAccount user = userAccountMapper.findByUsername(request);
        if (user == null || !passwordEncoder.matches(request.password(), user.password())) {
            return AuthResult.fail(isSecure, ResStatus.INVALID_ID_PW);
        }

        // 2. 토큰 생성
        String accessToken = jwtUtil.createAccessToken(user.username());
        RefreshToken refreshToken = jwtUtil.createRefreshToken(user.username());

        // 3. UPSERT 실행 (하나의 쿼리로 처리)
        UserSession session = UserSession.builder()
                .userNo(user.userNo())
                .platform(header.platform())
                .deviceId(header.deviceId())
                .jti(refreshToken.jti())
                .issuedAt(refreshToken.issuedAt())
                .expiresAt(refreshToken.expiration())
                .userAgent(header.userAgent())
                .ip(header.clientIp())
                .revoked(false)
                .build();

        // DB에서 알아서 PK 충돌 시 UPDATE를 수행함
        userSessionMapper.updateSession(session);

        return AuthResult.success(isSecure, accessToken, refreshToken);
    }

    public AuthResult refresh(boolean isSecure, String token, DefaultHeader header) {
        RefreshToken oldRefreshToken = jwtUtil.parseToken(token);
        if (oldRefreshToken == null || oldRefreshToken.tokenType() != TokenType.refresh) {
            return AuthResult.fail(isSecure, ResStatus.INVALID_TOKEN);
        }

        String username = oldRefreshToken.username();
        RefreshToken newRefreshToken = jwtUtil.createRefreshToken(username);

        // platform이 PK라면 쿼리 파라미터에 반드시 포함
        int affectedRows = userSessionMapper.rotateRefreshToken(Map.of(
                "oldJti", oldRefreshToken.jti(),
                "newJti", newRefreshToken.jti(),
                "issuedAt", newRefreshToken.issuedAt(),
                "expiresAt", newRefreshToken.expiration(),
                "ip", header.clientIp(),
                "userAgent", header.userAgent(),
                "platform", header.platform(),
                "deviceId", header.deviceId()
        ));

        if (affectedRows == 0) {
            log.warn("Refresh 실패 - 세션 불일치: user={}, platform={}, device={}",
                    username, header.platform(), header.deviceId());
            return AuthResult.fail(isSecure, ResStatus.SESSION_COMPROMISED);
        }

        String accessToken = jwtUtil.createAccessToken(username);

        return AuthResult.success(isSecure, accessToken, newRefreshToken);
    }

    public void logout(String token) {
        RefreshToken refreshToken = jwtUtil.parseToken(token);
        if (refreshToken != null && refreshToken.jti() != null) {
            // 영향을 받은 행의 수를 반환받음
            int affectedRows = userSessionMapper.revokeByJti(refreshToken.jti());

            if (affectedRows == 0) {
                log.warn("이미 로그아웃되었거나 존재하지 않는 세션입니다. JTI: {}", refreshToken.jti());
            }
        }
    }

    // 🛠️ Helper Methods
    private ResponseCookie createRefreshTokenCookie(boolean isSecure, RefreshToken refreshToken) {
//        boolean isSecure = request.isSecure();

        return ResponseCookie.from("refreshToken", refreshToken.token())
                .httpOnly(true)
                .secure(isSecure)
                .path("/")
                .maxAge(refreshToken.duration()) // Duration 객체를 바로 받아서 처리
                .sameSite(isSecure ? "None" : "Lax")
                .build();
    }

    private ResponseCookie deleteRefreshTokenCookie(boolean isSecure) {
//        boolean isSecure = request.isSecure();

        return ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(isSecure)
                .path("/")
                .maxAge(Duration.ZERO)
                .sameSite(isSecure ? "None" : "Lax")
                .build();
    }
}