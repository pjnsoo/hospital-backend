package org.hospital.service.auth;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
//@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class LoginRequest {
    private String userId;
    private String passWd;
}