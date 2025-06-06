package org.example.backend.domain.auth.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.domain.auth.config.CustomSecurityUserDetails;
import org.example.backend.domain.user.entity.User;
import org.example.backend.domain.user.entity.UserRole;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    // ✅ 인증 제외할 정확한 경로만 명시
    private static final List<String> EXCLUDE_URLS = List.of(
            "/auth/login",
            "/auth/join",
            "/auth/refresh",
            "/auth/find-id",
            "/auth/reset-password",
            "/auth/check");

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        boolean excluded = EXCLUDE_URLS.stream()
                .anyMatch(exclude -> path.equals(exclude) || path.startsWith(exclude + "/"));
        if (excluded) {
            log.debug("⛔ JWTFilter 제외 경로: {}", path);
        } else {
            log.debug("✅ JWTFilter 적용 경로: {}", path);
        }
        return excluded;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String token = null;

        // 1. Authorization 헤더 우선
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            token = authorization.substring(7);
        }

        // 2. 헤더 없을 경우 accessToken 쿠키에서 가져오기
        if (token == null && request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if (cookie.getName().equals("accessToken")) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token == null) {
            log.warn("⚠️ JWT 토큰 없음. 경로: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        if (jwtUtil.isExpired(token)) {
            log.warn("⛔ JWT 토큰 만료됨: {}", token);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("JWT expired");
            return;
        }

        Long userId = jwtUtil.getUserId(token);
        String email = jwtUtil.getLoginId(token);
        String roleStr = jwtUtil.getRole(token);

        if (email == null || roleStr == null) {
            log.warn("⛔ JWT 토큰 파싱 실패: email or role is null");
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ SecurityContext가 비어 있는 경우에만 설정
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            User user = new User();
            user.setUserId(userId);
            user.setEmail(email);
            user.setRole(UserRole.valueOf(roleStr));

            CustomSecurityUserDetails userDetails = new CustomSecurityUserDetails(user);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authToken);
            log.info("🔐 인증 완료: {}", email);
        }

        filterChain.doFilter(request, response);
    }
}
