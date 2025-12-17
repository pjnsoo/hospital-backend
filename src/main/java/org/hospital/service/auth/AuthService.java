package org.hospital.service.auth;

import lombok.RequiredArgsConstructor;
import org.hospital.service.dto.AuthTokenDto;
import org.hospital.service.dto.LoginCommand;
import org.hospital.model.mapper.UserSessionMapper;
import org.hospital.model.table.UserAccount;
import org.hospital.model.table.UserSession;
import org.hospital.model.mapper.UserAccountMapper;
import org.hospital.util.jwt.JwtUtil;
import org.hospital.util.jwt.JwtVo;
import org.hospital.util.jwt.TokenType;
import org.springframework.security.crypto.password.PasswordEncoder; // 추가됨
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final UserSessionMapper userSessionMapper;
    private final UserAccountMapper userAccountMapper;

    // [추가] 비밀번호 암호화/검증을 위한 인코더 주입
    // (SecurityConfig에서 Bean으로 등록되어 있어야 함)
    private final PasswordEncoder passwordEncoder;

    // 1. 로그인
    @Transactional
    public AuthTokenDto login(LoginCommand command) {
        // 1-1. 사용자 조회
        UserAccount user = userAccountMapper.findByUsername(command.getUsername());

        // 1-2. 사용자 존재 여부 및 비밀번호 일치 여부 확인
        // 보안상 이유로 'ID가 없는 경우'와 '비번이 틀린 경우'의 메시지를 통일하는 것이 좋습니다.
        if (user == null || !passwordEncoder.matches(command.getPassword(), user.getPassword())) {
            throw new RuntimeException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        // --- 검증 완료, 토큰 발급 시작 ---

        String accessToken = jwtUtil.createAccessToken(user.getUsername());
        JwtVo refreshTokenVo = jwtUtil.createRefreshToken(user.getUsername());

        // DB 저장 (세션 정책 로직은 추후 적용, 현재는 단순 Insert)
        UserSession session = UserSession.builder()
                .userNo(user.getUserNo())
                .jti(refreshTokenVo.getJti())
                .platform(command.getDeviceType()) // DTO 필드명에 맞게 사용
                .deviceId(command.getDeviceId())
                .userAgent(command.getUserAgent())
                .ip(command.getClientIp())
                .issuedAt(refreshTokenVo.getIssuedAt())
                .expiresAt(refreshTokenVo.getExpiration())
                .revoked(false)
                .build();

        userSessionMapper.insert(session);

        return AuthTokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenVo.getToken())
                .refreshTokenDuration(jwtUtil.getRefreshTokenDuration())
                .build();
    }

    // 2. 리프레시
    @Transactional
    public AuthTokenDto refresh(String oldRefreshToken, String ip, String userAgent) {
        JwtVo jwtVo = jwtUtil.parseToken(oldRefreshToken);

        if (jwtVo == null || jwtVo.getTokenType() != TokenType.refresh) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        // 유저 확인
        UserAccount user = userAccountMapper.findByUsername(jwtVo.getUsername());
        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        // 새 토큰 발급 (Rotation)
        String newAccessToken = jwtUtil.createAccessToken(user.getUsername());
        JwtVo newRefreshToken = jwtUtil.createRefreshToken(user.getUsername());

        // 기존 토큰 무효화 로직이 필요하다면 추가 (revokeByJti)

        // 새 세션 기록
        UserSession newSession = UserSession.builder()
                .userNo(user.getUserNo())
                .jti(newRefreshToken.getJti())
                .ip(ip)
                .userAgent(userAgent)
                .issuedAt(newRefreshToken.getIssuedAt())
                .expiresAt(newRefreshToken.getExpiration())
                .revoked(false)
                .build();

        userSessionMapper.insert(newSession);

        return AuthTokenDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .refreshTokenDuration(jwtUtil.getRefreshTokenDuration())
                .build();
    }

    // 3. 로그아웃
    @Transactional
    public void logout(String refreshToken) {
        JwtVo jwtVo = jwtUtil.parseToken(refreshToken);
        if (jwtVo != null && jwtVo.getJti() != null) {
            userSessionMapper.revokeByJti(jwtVo.getJti());
        }
    }
}