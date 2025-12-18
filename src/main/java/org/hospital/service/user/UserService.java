package org.hospital.service.user;

import lombok.RequiredArgsConstructor;
import org.hospital.model.enums.UserRole;
import org.hospital.model.enums.UserStatus;
import org.hospital.model.mapper.UserAccountMapper;
import org.hospital.model.table.UserAccount;
import org.hospital.service.dto.UserSignUpRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserAccountMapper userAccountMapper;
    private final PasswordEncoder passwordEncoder;     // 비밀번호 암호화용

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

    public void registerUser(UserSignUpRequest req) {
        // 1. 아이디 중복 체크 (선택)
        if (userAccountMapper.existsByUsername(req.getUsername())) {
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        }

        // 2. 비밀번호 암호화 (절대 평문으로 저장 금지!)
        String encodedPassword = passwordEncoder.encode(req.getPassword());

        // 3. DB 저장 (user_account 테이블)
        UserAccount newUser = UserAccount.builder()
                .username(req.getUsername())
                .password(encodedPassword)
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .build();

        userAccountMapper.insert(newUser);
    }

    // 회원가입, 비밀번호 변경 등의 비즈니스 로직들...
}
