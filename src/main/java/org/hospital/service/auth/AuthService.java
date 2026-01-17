package org.hospital.service.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hospital.model.mapper.UserAccountMapper;
import org.hospital.model.table.UserAccount;
import org.hospital.service.ApiResponse;
import org.hospital.service.security.SecurityUserDetails;
import org.hospital.service.ResReason;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserAccountMapper userAccountMapper;
    private final PasswordEncoder passwordEncoder;

    public ApiResponse<?> login(HttpServletRequest httpReq, LoginRequest requestBody) {
        // 1. 사용자 조회 및 비밀번호 검증
        UserAccount user = userAccountMapper.findByUsername(requestBody);
        if (user == null || !passwordEncoder.matches(requestBody.getPassWd(), user.getPassWd())) {
            return ApiResponse.of(ResReason.INVALID_ID_PW, null);
        }

        // 민감 정보 제거
        user.setPassWd(null);
        user.setPassSalt(null);

        // 2. 인증 객체 생성
        SecurityUserDetails userDetails = new SecurityUserDetails(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        // 3. [중요] 기존 세션 무효화 및 새 세션 생성 (보안)
        HttpSession session = httpReq.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        session = httpReq.getSession(true); // 새 세션 생성

        // 4. SecurityContext 생성 및 세션 저장
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);

        // 현재 쓰레드의 Holder에도 설정 (로그인 요청 처리 중 Principal 사용을 위해)
        SecurityContextHolder.setContext(context);

        // [핵심] 세션에 스프링 시큐리티 컨텍스트 박기
        session.setAttribute("SPRING_SECURITY_CONTEXT", context);

        return ApiResponse.of(ResReason.SUCCESS, userDetails);
    }
}