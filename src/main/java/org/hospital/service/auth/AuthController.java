package org.hospital.service.auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hospital.service.ApiResponse;
import org.hospital.service.BaseController;
import org.hospital.service.DefaultHeader;
import org.hospital.service.ResReason;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        boolean isSecure = httpReq.isSecure();
        DefaultHeader header = getHeader(httpReq);

        AuthResult authResult = authService.login(isSecure, header, request);
        ApiResponse<?> response = authResult.response();
        HttpStatus status = response.reason().httpStatus;

        return ResponseEntity.status(status)
                .header(HttpHeaders.SET_COOKIE, authResult.cookie())
                .body(response);
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

        boolean isSecure = httpReq.isSecure();
        DefaultHeader header = getHeader(httpReq);

        try {
            AuthResult authResult = authService.refresh(isSecure, refreshToken, header);

            ApiResponse<?> response = authResult.response();
            HttpStatus status = response.reason().httpStatus;

            return ResponseEntity.status(status)
                    .header(HttpHeaders.SET_COOKIE, authResult.cookie())
                    .body(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .header(HttpHeaders.SET_COOKIE, authResult.cookie())
                    .body(ApiResponse.of(ResReason.INTERNAL_ERROR, null));
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

        boolean isSecure = httpReq.isSecure();

        // 쿠키 삭제는 무조건 실행 (그래야 사용자 화면이 바뀜)
        ResponseCookie cookie = authService.deleteRefreshTokenCookie(isSecure);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.of(ResReason.LOG_OUT, null));
    }

    private String getOrDefault(String value, String defaultValue) {
        return (value != null && !value.isBlank()) ? value : defaultValue;
    }
}