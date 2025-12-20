package org.hospital.service;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ResStatus {
    SUCCESS("성공"),
    INVALID_ID_PW("아이디 또는 비밀번호가 일치하지 않습니다."),
    INVALID_TOKEN("유효하지 않은 토큰입니다."),
    SESSION_COMPROMISED("보안 위험이 감지되었습니다. 다시 로그인해주세요."),
    ;

    public final String message;
}
