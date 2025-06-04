package org.example.backend.domain.auth.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.domain.user.entity.User;
import org.example.backend.domain.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        // ✅ CustomOauth2UserDetails로 캐스팅
        CustomOauth2UserDetails customUser = (CustomOauth2UserDetails) authentication.getPrincipal();
        String email = customUser.getUsername(); // DB에 저장된 email (예: "kakao_123456")

        log.info("✅ OAuth2 로그인 성공: {}", email);

        User user = userRepository.findByEmail(email).orElse(null);

        if (user != null) {
            if (user.isOnboardingCompleted()) {
                log.info("🔁 온보딩 완료 → 메인으로 리디렉션");
                response.sendRedirect("/");
            } else {
                log.info("🧾 온보딩 미완료 → 온보딩 페이지로 리디렉션");
                response.sendRedirect("/auth/onboarding");
            }
        } else {
            log.warn("❌ DB에서 사용자 조회 실패: {}", email);
            response.sendRedirect("/auth/login?error=true");
        }
    }
}
