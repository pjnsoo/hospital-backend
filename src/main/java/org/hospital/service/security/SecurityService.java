package org.hospital.service.security;

import lombok.RequiredArgsConstructor;
import org.hospital.model.mapper.UserAccountMapper;
import org.hospital.model.table.UserAccount;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityService implements UserDetailsService {

    private final UserAccountMapper userAccountMapper;

//    @PreAuthorize("hasRole('ADMIN')") // 역할
//    @PreAuthorize("hasAuthority('REGISTER_USER')") // 권한

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. DB에서 최신 계정 정보 조회 (안전을 위해 매번 조회)
        UserAccount user = userAccountMapper.findByUsername(username);

        // 2. 계정 자체가 존재하지 않는 경우
        if (user == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
        }

        // 3. 상태 체크 로직이 포함된 CustomUserDetails 반환
        return new SecurityUserDetails(user);
    }

}
