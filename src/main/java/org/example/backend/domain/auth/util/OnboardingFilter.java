package org.example.backend.domain.auth.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.domain.user.entity.User;
import org.example.backend.domain.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OnboardingFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();
        log.debug("[OnboardingFilter] 요청 URI: {}", uri);

        // ✅ 온보딩, 로그인, 정적 자원은 필터 제외
        if (uri.startsWith("/onboarding")
                || uri.startsWith("/auth/onboarding")
                || uri.startsWith("/auth/login")
                || uri.startsWith("/css")
                || uri.startsWith("/js")
                || uri.startsWith("/images")
                || uri.startsWith("/favicon")
                || uri.startsWith("/error")) {
            log.debug("[OnboardingFilter] 필터 제외 대상 URI → {}", uri);
            filterChain.doFilter(request, response);
            return;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            log.debug("[OnboardingFilter] 인증된 사용자 존재");

            Object principal = auth.getPrincipal();
            String email = null;

            if (principal instanceof UserDetails userDetails) {
                email = userDetails.getUsername();
            } else if (principal instanceof OAuth2User oAuth2User) {
                email = oAuth2User.getAttribute("email");
            }

            log.debug("[OnboardingFilter] 인증된 사용자 이메일: {}", email);

            if (email != null) {
                User user = userRepository.findByEmail(email).orElse(null);
                if (user != null) {
                    log.debug("[OnboardingFilter] 사용자 Onboarding 상태: {}", user.isOnboardingCompleted());

                    if (!user.isOnboardingCompleted()) {
                        log.warn("[OnboardingFilter] {} 온보딩 미완료 → /auth/onboarding 으로 리디렉션", email);
                        response.sendRedirect("/auth/onboarding");
                        return;
                    }
                } else {
                    log.warn("[OnboardingFilter] 이메일로 사용자 조회 실패: {}", email);
                }
            }
        } else {
            log.debug("[OnboardingFilter] 인증된 사용자 없음 또는 anonymousUser");
        }

        filterChain.doFilter(request, response);
    }
}
