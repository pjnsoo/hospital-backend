package org.hospital.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum ResReason {
    SUCCESS(HttpStatus.OK, "성공"),
    LOG_OUT(HttpStatus.OK, "로그아웃"),

    // 400: 클라이언트가 보낸 데이터 자체에 문제가 있음
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

    // 401: 인증 실패 (비번 틀림 등)
    INVALID_ID_PW(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 일치하지 않습니다."),

    // 500: 서버 내부 로직 에러
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

    public final HttpStatus httpStatus;
    public final String message;
}
