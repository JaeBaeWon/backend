package org.example.backend.domain.auth.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.backend.domain.user.entity.User;
import org.example.backend.domain.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OnboardingFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String uri = request.getRequestURI();

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String email = auth.getName();
            User user = userRepository.findByEmail(email).orElse(null);

            // 1. MANAGER는 /manager/performance/** 경로일 경우 온보딩 무시
            if (user != null && user.getRole().name().equals("MANAGER") && uri.startsWith("/manager/performance")) {
                filterChain.doFilter(request, response);
                return;
            }

            // 2. 일반 유저는 온보딩 미완료 시 /auth/onboarding 또는 /auth/logout 외 리다이렉트
            if (user != null && !user.isOnboardingCompleted()) {
                if (!uri.startsWith("/auth/onboarding") && !uri.startsWith("/auth/logout")) {
                    response.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
                    response.setHeader("Location", "/onboarding");
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
