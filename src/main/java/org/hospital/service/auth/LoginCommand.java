package org.hospital.service.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginCommand {
    private String username;
    private String password;
    private String clientIp;
    private String userAgent;
    private String deviceId;
    private String deviceType;
}