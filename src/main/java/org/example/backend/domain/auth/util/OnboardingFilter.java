package org.example.backend.domain.auth.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.domain.user.entity.User;
import org.example.backend.domain.user.entity.UserRole;
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

    private static final String[] EXCLUDED_PATHS = {
            "/auth/onboarding",
            "/auth/login",
            "/api/user",
            "/api/member",
            "/email",
            "/css",
            "/js",
            "/images",
            "/favicon",
            "/error"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();
        log.debug("[OnboardingFilter] 요청 URI: {}", uri);

        // 필터 제외 경로
        for (String path : EXCLUDED_PATHS) {
            if (uri.startsWith(path)) {
                log.debug("[OnboardingFilter] 필터 제외 대상 URI → {}", uri);
                filterChain.doFilter(request, response);
                return;
            }
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            log.debug("[OnboardingFilter] 인증된 사용자 존재");

            Object principal = auth.getPrincipal();
            String email = null;

            // 인증된 사용자가 UserDetails나 OAuth2User일 경우 이메일 추출
            if (principal instanceof UserDetails userDetails) {
                email = userDetails.getUsername(); // 기본 로그인 사용자
            } else if (principal instanceof OAuth2User oAuth2User) {
                Object attr = oAuth2User.getAttribute("email"); // OAuth2 로그인 사용자 이메일 추출
                email = attr != null ? attr.toString() : null;
            }

            log.debug("[OnboardingFilter] 인증된 사용자 이메일: {}", email);

            if (email != null) {
                User user = userRepository.findByEmail(email).orElse(null);

                if (user != null) {
                    log.debug("[OnboardingFilter] 사용자 ROLE: {}, Onboarding 상태: {}",
                            user.getRole(), user.isOnboardingCompleted());

                    // CONSUMER일 때만 온보딩 여부 체크
                    if (user.getRole() == UserRole.CONSUMER && !user.isOnboardingCompleted()) {
                        log.info("[OnboardingFilter] {} (CONSUMER) 온보딩 미완료 → /auth/onboarding 으로 리디렉션", email);
                        response.sendRedirect(request.getContextPath() + "/auth/onboarding");
                        return;
                    }
                } else {
                    log.info("[OnboardingFilter] 이메일로 사용자 조회 실패: {}", email);
                }
            }
        } else {
            log.debug("[OnboardingFilter] 인증된 사용자 없음 또는 anonymousUser");
        }

        filterChain.doFilter(request, response);
    }
}
