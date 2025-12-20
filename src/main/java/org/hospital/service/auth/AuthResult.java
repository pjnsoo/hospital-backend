package org.hospital.service.auth;

import org.hospital.component.jwt.RefreshToken;
import org.hospital.service.ApiResponse;
import org.hospital.service.ResStatus;
import org.springframework.http.ResponseCookie;

import java.time.Duration;
import java.util.Map;

public record AuthResult(
        ApiResponse<?> response,
        ResponseCookie cookie) {

    public static AuthResult success(boolean isSecure, String accessToken, RefreshToken refreshToken) {
        ApiResponse<?> successResponse = ApiResponse.of(ResStatus.SUCCESS, Map.of("accessToken", accessToken));

        ResponseCookie refreshCookie = createRefreshTokenCookie(
                isSecure, refreshToken.token(), refreshToken.duration());

        return new AuthResult(successResponse, refreshCookie);
    }

    public static AuthResult fail(boolean isSecure, ResStatus status) {
        ApiResponse<?> failResponse = ApiResponse.of(status, null);

        ResponseCookie deleteCookie = createRefreshTokenCookie(
                isSecure, "", Duration.ZERO);

        return new AuthResult(failResponse, deleteCookie);
    }

    public static ResponseCookie createRefreshTokenCookie(boolean isSecure, String refreshToken, Duration duration) {

        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(isSecure)
                .path("/")
                .maxAge(duration)
                .sameSite(isSecure ? "None" : "Lax")
                .build();
    }

}