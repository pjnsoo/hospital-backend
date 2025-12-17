package org.hospital.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserSignUpRequest {
    private String username;
    private String password;
    private String nickname;
}