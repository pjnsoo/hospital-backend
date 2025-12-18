package org.hospital.model.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserStatus {
    ACTIVE("정상", "계정이 활성화되어 서비스 이용이 가능한 상태"),
    DELETED("삭제", "관리자 삭제한 상태"),
    DORMANT("휴면", "장기간 미접속으로 인해 보호 조치된 상태"),
    LOCKED("잠금", "보안 정책 위반(비밀번호 오류 등)으로 일시 차단된 상태"),
    ;

    public final String label;
    public final String description;

}
