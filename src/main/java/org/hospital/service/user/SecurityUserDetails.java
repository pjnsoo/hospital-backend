package org.hospital.service.user;

import org.hospital.model.enums.UserStatus;
import org.hospital.model.table.UserAccount;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;

public record SecurityUserDetails(UserAccount userAccount) implements UserDetails {

    @Override
    public String getUsername() {
        return userAccount.getUsername();
    }

    @Override
    public String getPassword() {
        return userAccount.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 권한 정보 매핑 (예: ROLE_USER)
        return List.of(new SimpleGrantedAuthority("ROLE_" + userAccount.getRole().name()));
    }

    /**
     * [보안] 계정 활성화 여부 (예: 이메일 미인증, 관리자 강제 비활성화 등)
     */
    @Override
    public boolean isEnabled() {
        return userAccount.getStatus() == UserStatus.ACTIVE;
    }

    /**
     * [보안] 계정 잠금 여부 (예: 비밀번호 5회 오류 등)
     */
    @Override
    public boolean isAccountNonLocked() {
        return userAccount.getStatus() != UserStatus.LOCKED;
    }

    /**
     * [보안] 계정 만료 여부 (예: 장기 미접속 휴면 계정 등)
     */
    @Override
    public boolean isAccountNonExpired() {
        Instant accountExpiresAt = userAccount.getLastAccessAt()
                .plus(1, ChronoUnit.YEARS);

        return accountExpiresAt.isAfter(Instant.now());
    }

    /**
     * [보안] 비밀번호 만료 여부 (예: 90일마다 비번 변경 정책)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        Instant passwordExpiresAt = userAccount.getPwChangedAt()
                .plus(90, ChronoUnit.DAYS);

        return passwordExpiresAt.isAfter(Instant.now());
    }

    // 컨트롤러 편의성을 위해 추가하는 커스텀 메서드
    public Long getUserNo() {
        return userAccount.getUserNo();
    }
}
