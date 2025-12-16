package org.hospital;

import org.hospital.entity.UserSession;
import org.hospital.util.jwt.JwtVo;

public interface RefreshTokenService11 {
    void saveNew(UserSession record);
    void revoke(String jti);
//    boolean isValid(String username, String jti, Date expiresAt);
    boolean isValid(JwtVo jwt);
}
