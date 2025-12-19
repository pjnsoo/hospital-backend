package org.hospital.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ==========================================
    // 1. 회원가입 (Sign Up)
    // ==========================================
    @PostMapping("/signup") // URL: POST /api/users/signup
    public ResponseEntity<?> signUp(@RequestBody UserSignUpRequest req) {

        // 유효성 검사 (아이디 중복 등) 및 DB Insert 로직은 Service에 위임
        userService.registerUser(req);

        return ResponseEntity.ok("회원가입 성공");
    }
}
