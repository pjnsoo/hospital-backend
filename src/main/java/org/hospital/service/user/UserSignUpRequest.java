package org.hospital.service.user;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
//@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class UserSignUpRequest {
    private String userId;
    private String passWd;
}