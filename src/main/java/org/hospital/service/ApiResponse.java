package org.hospital.service;

import org.springframework.http.HttpStatus;

public record ApiResponse<T>(
        boolean success,
        ResReason reason,
        String message,
        T data
) {

    public static <T> ApiResponse<T> of(ResReason code, T data) {
        boolean success = code.httpStatus == HttpStatus.OK;
        return new ApiResponse<>(success, code, code.message, data);
    }
}