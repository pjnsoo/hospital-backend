package org.hospital.auth;

import org.hospital.entity.UserSession;
import org.hospital.mapper.UserSessionMapper;
import org.hospital.util.jwt.JwtVo;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Primary
public class RefreshTokenService {

    private final UserSessionMapper mapper;

    public RefreshTokenService(UserSessionMapper mapper) {
        this.mapper = mapper;
    }

    public void saveNew(UserSession record) {
        mapper.insert(record);
    }

    public void revoke(String jti) {
        mapper.revokeByJti(jti);
    }

    public boolean isValid(JwtVo jwt) {
        Instant expiration = jwt.getExpiration();
        // expiration이 null이면 유효하지 않다고 판단
        if (expiration == null) return false;

        // 현재 시간과 비교
        return Instant.now().isBefore(expiration);
    }


}