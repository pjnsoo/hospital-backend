package org.hospital.service;

public record ApiResponse<T>(
        boolean success,
        ResStatus status,
        String message,
        T data
) {

    public static <T> ApiResponse<T> of(ResStatus code, T data) {
        boolean success = code == ResStatus.SUCCESS;
        return new ApiResponse<>(success, code, code.message, data);
    }


    public static <T> ApiResponse<T> fail(String message, T data) {
        return new ApiResponse<>(false, message, data);
    }
}