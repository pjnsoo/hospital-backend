package org.hospital.config;

import org.hospital.component.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                // 1. CORS 설정 (기존 설정 유지)
                .cors(cors -> {
                })
                // 2. CSRF 설정 (하나의 블록으로 통합)
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers("/api/auth/**", "/local/**") // 두 경로 모두 제외
                )
                // 3. 세션 설정 (Stateless)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 4. 권한 설정 (좁은 범위부터 넓은 범위 순서로!)
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/api/auth/**").permitAll()   // 인증 관련 API 허용
//                        .requestMatchers("/local/**").permitAll() // 로컬 암호화 API 허용
                                .requestMatchers("/local/**").access((authentication, context) -> {
                                    String remoteAddress = context.getRequest().getRemoteAddr();
                                    // IPv4 로컬과 IPv6 로컬 모두 허용
                                    boolean isLocal = "127.0.0.1".equals(remoteAddress) || "0:0:0:0:0:0:0:1".equals(remoteAddress);
                                    return new AuthorizationDecision(isLocal);
                                })
                                .anyRequest().authenticated()                  // 나머지는 다 막음
                )
                // 5. JWT 필터 추가
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
