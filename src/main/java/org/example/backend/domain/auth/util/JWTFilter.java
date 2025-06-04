package org.example.backend.domain.auth.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.backend.domain.auth.config.CustomSecurityUserDetails;
import org.example.backend.domain.user.entity.User;
import org.example.backend.domain.user.entity.UserRole;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Component

public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    // ✅ 인증 제외 URL 목록
    private static final List<String> EXCLUDE_URLS = List.of(
            "/auth/login", "/auth/join", "/auth/refresh",
            "/auth/find-id", "/auth/reset-password", "/auth/check");

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return EXCLUDE_URLS.stream().anyMatch(path::startsWith);
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
            filterChain.doFilter(request, response);
            return;
        }

        if (jwtUtil.isExpired(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("JWT expired");
            return;
        }

        Long userId = jwtUtil.getUserId(token);
        String email = jwtUtil.getLoginId(token);
        String roleStr = jwtUtil.getRole(token);

        if (email == null || roleStr == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            User user = new User();
            user.setUserId(userId);
            user.setEmail(email);
            user.setRole(UserRole.valueOf(roleStr));

            CustomSecurityUserDetails userDetails = new CustomSecurityUserDetails(user);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null,
                    userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }

}
