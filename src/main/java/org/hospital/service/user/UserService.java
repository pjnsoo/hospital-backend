package org.hospital.service.user;

import lombok.RequiredArgsConstructor;
import org.hospital.model.mapper.UserAccountMapper;
import org.hospital.model.table.UserAccount;
import org.hospital.model.table.enums.UserRole;
import org.hospital.model.table.enums.UserStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserAccountMapper userAccountMapper;
    private final PasswordEncoder passwordEncoder;

    public void registerUser(UserSignUpRequest req) {
        // 1. 아이디 중복 체크 (선택)
        if (userAccountMapper.existsByUsername(req.getUserId())) {
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        }

        // 2. 비밀번호 암호화 (절대 평문으로 저장 금지!)
//        String encodedPassword = passwordEncoder.encode(req.getPassWd());

        // 3. DB 저장 (user_account 테이블)
        UserAccount newUser = UserAccount.builder()
                .userId(req.getUserId())
//                .passWd(encodedPassword)
                .passWd(req.getPassWd())
                .userRole(UserRole.USER)
                .activeYn("Y")
//                .status(UserStatus.ACTIVE)
                .build();

        userAccountMapper.insert(newUser);
    }

    // 회원가입, 비밀번호 변경 등의 비즈니스 로직들...
}
