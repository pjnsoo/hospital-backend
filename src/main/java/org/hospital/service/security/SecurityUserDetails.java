package org.hospital.service.security;

import org.hospital.model.table.UserAccount;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.Collection;
import java.util.List;

public record SecurityUserDetails(UserAccount userAccount) implements UserDetails {

    @Override
    public String getUsername() {
        return userAccount.getUserId();
    }

    @Override
    public String getPassword() {
        return userAccount.getPassWd();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 권한 정보 매핑 (예: ROLE_USER)
//        return List.of(new SimpleGrantedAuthority("ROLE_" + userAccount.getUserRole().name()));
        return List.of(new SimpleGrantedAuthority("ROLE_U"));
    }

    /**
     * [보안] 계정 활성화 여부 (예: 이메일 미인증, 관리자 강제 비활성화 등)
     */
    @Override
    public boolean isEnabled() {
//        return userAccount.getStatus() == UserStatus.ACTIVE;
//        return "Y".equals(userAccount.getActiveYn());
        return true;
    }

    /**
     * [보안] 계정 잠금 여부 (예: 비밀번호 5회 오류 등)
     */
    @Override
    public boolean isAccountNonLocked() {
//        return userAccount.getStatus() != UserStatus.LOCKED;
//        return userAccount.getLoginCnt() > 10;
        return true;
    }

    /**
     * [보안] 계정 만료 여부 (예: 장기 미접속 휴면 계정 등)
     */
    @Override
    public boolean isAccountNonExpired() {
//        Instant accountExpiresAt = userAccount.getLastAccessAt()
//                .plus(1, ChronoUnit.YEARS);

//        return accountExpiresAt.isAfter(Instant.now());
        return true;
    }

    /**
     * [보안] 비밀번호 만료 여부 (예: 90일마다 비번 변경 정책)
     */
    @Override
    public boolean isCredentialsNonExpired() {
//        Instant passwordExpiresAt = userAccount.getPwChangedAt()
//                .plus(90, ChronoUnit.DAYS);

//        return passwordExpiresAt.isAfter(Instant.now());
        return true;
    }

    // 컨트롤러 편의성을 위해 추가하는 커스텀 메서드
//    public Long getUserNo() {
//        return userAccount.getUserNo();
//    }
}
