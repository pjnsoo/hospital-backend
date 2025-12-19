package org.hospital.service;

import jakarta.servlet.http.HttpServletRequest;

public abstract class BaseController {

    protected DefaultHeader getHeader(HttpServletRequest request) {
        String clientIp = request.getHeader("X-FORWARDED-FOR");
        if (clientIp == null) {
            clientIp = request.getRemoteAddr();
        }

        DefaultHeader header = new DefaultHeader(
                clientIp,
                request.getHeader("User-Agent"),
                request.getHeader("platform"),
                request.getHeader("device-id")

        );

        if (header.platform() == null || header.deviceId() == null) {
            throw new IllegalArgumentException("필수 보안 헤더(Platform, Device-Id)가 누락되었습니다.");
        }

        return header;
    }
}
