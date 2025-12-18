package org.hospital.model.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserRole {
    USER("사용자"),
    MANAGER("관리자"),
    SYSTEM_ADMIN("시스템 관리자"),
    ;

    public final String label;

}
