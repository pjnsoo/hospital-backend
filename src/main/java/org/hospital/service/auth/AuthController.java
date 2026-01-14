package org.hospital.service.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hospital.service.ApiResponse;
import org.hospital.service.BaseController;
import org.hospital.service.ResReason;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    public ResponseEntity<?> login(HttpServletRequest httpReq, @RequestBody LoginRequest requestBody) {
        ApiResponse<?> response = authService.login(httpReq, requestBody);
        HttpStatus status = response.reason().httpStatus;

        return ResponseEntity.status(status).body(response);
    }

    // ==========================================
    // 2. 로그아웃 (Log Out)
    // ==========================================
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest httpReq) {
        HttpSession session = httpReq.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok()
                .body(ApiResponse.of(ResReason.LOG_OUT, null));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        // 1. 세션이 없거나 인증되지 않은 경우
        if (userDetails == null) {
            // 인터셉터에서 401을 처리할 수 있도록 응답
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 2. 인증된 경우 사용자 정보 반환 (필요한 정보만 담은 DTO 권장)
        ApiResponse<?> userResponse = ApiResponse.of(ResReason.SUCCESS, userDetails);

        return ResponseEntity.ok(userResponse);
    }
}