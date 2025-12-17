package org.hospital.service.auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hospital.service.dto.AuthTokenDto;
import org.hospital.service.dto.LoginCommand;
import org.hospital.service.dto.LoginRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ==========================================
    // 1. 로그인 (Sign In)
    // ==========================================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request,
                                   HttpServletRequest httpReq) {

        // 헤더 파싱 및 Command 객체 조립 (Over-posting 방지)
        LoginCommand command = LoginCommand.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .clientIp(httpReq.getRemoteAddr())
                .userAgent(httpReq.getHeader("User-Agent"))
                // 헤더가 없으면 기본값 설정 (방어 로직)
                .deviceType(getOrDefault(httpReq.getHeader("X-Device-Type"), "WEB"))
                .deviceId(getOrDefault(httpReq.getHeader("X-Device-Id"), UUID.randomUUID().toString()))
                .build();

        AuthTokenDto tokenDto = authService.login(command);

        // 쿠키 설정 (DTO에 있는 Duration을 그대로 사용)
        ResponseCookie cookie = createRefreshTokenCookie(
                tokenDto.getRefreshToken(),
                httpReq,
                tokenDto.getRefreshTokenDuration()
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("accessToken", tokenDto.getAccessToken()));
    }

    // ==========================================
    // 2. 리프레시 (Refresh)
    // ==========================================
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(value = "refreshToken", required = false) String refreshToken,
                                     HttpServletRequest httpReq) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(401).build();
        }

        try {
            AuthTokenDto tokenDto = authService.refresh(
                    refreshToken,
                    httpReq.getRemoteAddr(),
                    httpReq.getHeader("User-Agent")
            );

            ResponseCookie cookie = createRefreshTokenCookie(
                    tokenDto.getRefreshToken(),
                    httpReq,
                    tokenDto.getRefreshTokenDuration()
            );

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(Map.of("accessToken", tokenDto.getAccessToken()));

        } catch (Exception e) {
            // 실패 시 쿠키 삭제
            ResponseCookie deleteCookie = createRefreshTokenCookie("", httpReq, Duration.ZERO);
            return ResponseEntity.status(401)
                    .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                    .build();
        }
    }

    // ==========================================
    // 3. 로그아웃 (Log Out)
    // ==========================================
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(value = "refreshToken", required = false) String refreshToken,
                                    HttpServletRequest httpReq) {
        if (refreshToken != null) {
            authService.logout(refreshToken);
        }

        ResponseCookie cookie = createRefreshTokenCookie("", httpReq, Duration.ZERO);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    // 🛠️ Helper Methods
    private ResponseCookie createRefreshTokenCookie(String value, HttpServletRequest request, Duration maxAge) {
        boolean isSecure = request.isSecure();

        return ResponseCookie.from("refreshToken", value)
                .httpOnly(true)
                .secure(isSecure)
                .path("/")
                .maxAge(maxAge) // Duration 객체를 바로 받아서 처리
                .sameSite(isSecure ? "None" : "Lax")
                .build();
    }

    private String getOrDefault(String value, String defaultValue) {
        return (value != null && !value.isBlank()) ? value : defaultValue;
    }
}