package org.hospital.service.auth;

import org.hospital.service.ApiResponse;
import org.hospital.service.ResReason;
import org.hospital.service.security.SecurityUserDetails;

public record AuthResult(ApiResponse<?> response) {

    public static AuthResult success(SecurityUserDetails userDetails) {
        ApiResponse<?> successResponse = ApiResponse.of(ResReason.SUCCESS, userDetails);
        return new AuthResult(successResponse);
    }

    public static AuthResult fail(ResReason status) {
        ApiResponse<?> failResponse = ApiResponse.of(status, null);
        return new AuthResult(failResponse);
    }
}