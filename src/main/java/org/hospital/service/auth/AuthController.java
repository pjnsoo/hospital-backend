package org.hospital.service.auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hospital.service.BaseController;
import org.hospital.service.DefaultHeader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController extends BaseController {

    private final AuthService authService;

    // ==========================================
    // 1. 로그인 (Sign In)
    // ==========================================
    @PostMapping("/login")
    public ResponseEntity<?> login(HttpServletRequest httpReq, @RequestBody LoginRequest request) {
        DefaultHeader header = getHeader(httpReq);

        AuthInfo authInfo = authService.login(header, request);

        // 쿠키 설정 (DTO에 있는 Duration을 그대로 사용)
        ResponseCookie cookie = createRefreshTokenCookie(httpReq, authInfo);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("accessToken", authInfo.accessToken()));
    }

    // ==========================================
    // 2. 리프레시 (Refresh)
    // ==========================================
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(value = "refreshToken", required = false) String refreshToken,
                                     HttpServletRequest httpReq) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        DefaultHeader header = getHeader(httpReq);

        try {

            AuthInfo authInfo = authService.refresh(refreshToken, header);

            ResponseCookie cookie = createRefreshTokenCookie(httpReq, authInfo);

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(Map.of("accessToken", authInfo.accessToken()));

        } catch (Exception e) {
            // 실패 시 쿠키 삭제
            ResponseCookie deleteCookie = deleteRefreshTokenCookie(httpReq);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
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
        try {
            if (refreshToken != null && !refreshToken.isBlank()) {
                authService.logout(refreshToken);
            }
        } catch (Exception e) {
            // DB 에러가 나더라도 로그만 찍고, 클라이언트의 쿠키는 반드시 지워줘야 함
            log.error("로그아웃 DB 처리 실패: {}", e.getMessage());
        }

        // 쿠키 삭제는 무조건 실행 (그래야 사용자 화면이 바뀜)
        ResponseCookie cookie = deleteRefreshTokenCookie(httpReq);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("message", "success"));
    }

    // 🛠️ Helper Methods
    private ResponseCookie createRefreshTokenCookie(HttpServletRequest request, AuthInfo authInfo) {
        boolean isSecure = request.isSecure();

        return ResponseCookie.from("refreshToken", authInfo.refreshToken())
                .httpOnly(true)
                .secure(isSecure)
                .path("/")
                .maxAge(authInfo.refreshTokenDuration()) // Duration 객체를 바로 받아서 처리
                .sameSite(isSecure ? "None" : "Lax")
                .build();
    }

    private ResponseCookie deleteRefreshTokenCookie(HttpServletRequest request) {
        boolean isSecure = request.isSecure();

        return ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(isSecure)
                .path("/")
                .maxAge(Duration.ZERO)
                .sameSite(isSecure ? "None" : "Lax")
                .build();
    }

    private String getOrDefault(String value, String defaultValue) {
        return (value != null && !value.isBlank()) ? value : defaultValue;
    }
}