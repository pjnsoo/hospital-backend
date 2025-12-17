package org.hospital.service.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Duration;

@Getter
@Builder
public class AuthTokenDto {
    private String accessToken;
    private String refreshToken;

    // [핵심] long 대신 Duration 객체를 그대로 전달하여 의미를 명확히 함
    private Duration refreshTokenDuration;
}