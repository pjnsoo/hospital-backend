package org.hospital.service;

import lombok.RequiredArgsConstructor;
import org.hospital.dto.UserSignUpRequest;
import org.hospital.entity.UserAccount;
import org.hospital.mapper.UserAccountMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserAccountMapper userAccountMapper; // 유저 정보 테이블 매퍼
    private final PasswordEncoder passwordEncoder;     // 비밀번호 암호화용

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
                .nickname(req.getNickname())
                .role("ROLE_USER")
                .status("ACTIVE")
                .build();

        userAccountMapper.insert(newUser);
    }
}
