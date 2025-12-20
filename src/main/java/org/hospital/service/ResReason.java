package org.hospital.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum ResReason {
    SUCCESS(HttpStatus.OK, "성공"),
    LOG_OUT(HttpStatus.OK, "로그아웃"),

    // 400: 클라이언트가 보낸 데이터 자체에 문제가 있음
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

    // 401: 인증 실패 (비번 틀림, 토큰 만료 등)
    INVALID_ID_PW(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 일치하지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),

    // 403: 인증은 됐으나 권한이 없거나, 보안상 접근 거부
    // RTR 충돌은 세션이 오염된 것이므로 '거부' 의미가 강한 403이 적합할 수 있으나,
    // 재로그인을 유도해야 하므로 401을 써도 무방합니다. (여기선 보안 강조를 위해 401 유지)
    SESSION_COMPROMISED(HttpStatus.UNAUTHORIZED, "보안 위험이 감지되었습니다. 다시 로그인해주세요."),

    // 500: 서버 내부 로직 에러
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

    public final HttpStatus httpStatus;
    public final String message;
}
