package org.hospital.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hospital.service.dto.AuthTokenDto;
import org.hospital.model.mapper.UserSessionMapper;
import org.hospital.model.table.UserAccount;
import org.hospital.model.table.UserSession;
import org.hospital.model.mapper.UserAccountMapper;
import org.hospital.util.jwt.JwtUtil;
import org.hospital.util.jwt.JwtVo;
import org.hospital.util.jwt.TokenType;
import org.springframework.security.crypto.password.PasswordEncoder; // 추가됨
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final UserSessionMapper userSessionMapper;
    private final UserAccountMapper userAccountMapper;
    private final PasswordEncoder passwordEncoder;

    public AuthTokenDto login(LoginCommand command) {
        // 1. 사용자 조회 및 비밀번호 검증 (동일)
        UserAccount user = userAccountMapper.findByUsername(command.getUsername());
        if (user == null || !passwordEncoder.matches(command.getPassword(), user.getPassword())) {
            throw new RuntimeException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        // 2. 토큰 생성
        String accessToken = jwtUtil.createAccessToken(user.getUsername());
        JwtVo refreshTokenVo = jwtUtil.createRefreshToken(user.getUsername());

        // 3. UPSERT 실행 (하나의 쿼리로 처리)
        UserSession session = UserSession.builder()
                .userNo(user.getUserNo())
                .platform(command.getDeviceType())
                .deviceId(command.getDeviceId())
                .jti(refreshTokenVo.getJti())
                .issuedAt(refreshTokenVo.getIssuedAt())
                .expiresAt(refreshTokenVo.getExpiration())
                .userAgent(command.getUserAgent())
                .ip(command.getClientIp())
                .revoked(false)
                .build();

        // DB에서 알아서 PK 충돌 시 UPDATE를 수행함
        userSessionMapper.updateSession(session);

        return AuthTokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenVo.getToken())
                .refreshTokenDuration(jwtUtil.getRefreshTokenDuration())
                .build();
    }

    public AuthTokenDto refresh(String oldRefreshToken, String ip, String userAgent, String deviceId, String platform) {
        JwtVo oldJwtVo = jwtUtil.parseToken(oldRefreshToken);
        if (oldJwtVo == null || oldJwtVo.getTokenType() != TokenType.refresh) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        String username = oldJwtVo.getUsername();
        JwtVo newRefreshTokenVo = jwtUtil.createRefreshToken(username);

        // platform이 PK라면 쿼리 파라미터에 반드시 포함
        int affectedRows = userSessionMapper.rotateRefreshToken(Map.of(
                "oldJti", oldJwtVo.getJti(),
                "newJti", newRefreshTokenVo.getJti(),
                "issuedAt", newRefreshTokenVo.getIssuedAt(),
                "expiresAt", newRefreshTokenVo.getExpiration(),
                "ip", ip,
                "userAgent", userAgent,
                "platform", platform,
                "deviceId", deviceId
        ));

        if (affectedRows == 0) {
            log.warn("Refresh 실패 - 세션 불일치: user={}, platform={}, device={}", username, platform, deviceId);
            throw new RuntimeException("보안 위험이 감지되었습니다. 다시 로그인해주세요.");
        }

        return AuthTokenDto.builder()
                .accessToken(jwtUtil.createAccessToken(username))
                .refreshToken(newRefreshTokenVo.getToken())
                .refreshTokenDuration(jwtUtil.getRefreshTokenDuration())
                .build();
    }

    public void logout(String refreshToken) {
        JwtVo jwtVo = jwtUtil.parseToken(refreshToken);
        if (jwtVo != null && jwtVo.getJti() != null) {
            // 영향을 받은 행의 수를 반환받음
            int affectedRows = userSessionMapper.revokeByJti(jwtVo.getJti());

            if (affectedRows == 0) {
                log.warn("이미 로그아웃되었거나 존재하지 않는 세션입니다. JTI: {}", jwtVo.getJti());
            }
        }
    }
}