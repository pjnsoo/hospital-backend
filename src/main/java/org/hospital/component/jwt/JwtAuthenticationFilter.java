package org.hospital.component.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.hospital.service.security.SecurityService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final SecurityService securityService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            RefreshToken refreshToken = jwtUtil.parseToken(token);

            if (refreshToken != null && refreshToken.getTokenType() != TokenType.refresh) {
                // 1. 토큰에서는 식별자(username)만 꺼냅니다.
                String username = refreshToken.getUsername();

                // 2. "실시간성"을 위해 DB에서 유저와 권한 정보를 새로 가져옵니다.
                // 만약 유저가 삭제되었거나 정지되었다면 여기서 Exception이 발생하여 입구 컷 당합니다.
                UserDetails userDetails = securityService.loadUserByUsername(username);

                // 3. DB에서 갓 가져온 최신 권한(getAuthorities)을 시큐리티에 넣어줍니다.
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}