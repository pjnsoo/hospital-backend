package org.hospital.service;

import jakarta.servlet.http.HttpServletRequest;

public abstract class BaseController {

    protected DefaultHeader getHeader(HttpServletRequest request) {
        String clientIp = request.getHeader("X-FORWARDED-FOR");
        if (clientIp == null) {
            clientIp = request.getRemoteAddr();
        }

        return new DefaultHeader(
                clientIp,
                request.getHeader("User-Agent"),
                request.getHeader("platform"),
                request.getHeader("device-Id")
        );
    }
}
