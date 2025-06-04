package org.example.backend.domain.auth.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.domain.user.entity.User;
import org.example.backend.domain.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

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

        String email = authentication.getName();
        log.info("소셜 로그인 성공: {}", email);

        User user = userRepository.findByEmail(email).orElse(null);

        if (user != null) {
            if (user.isOnboardingCompleted()) {
                response.sendRedirect("/");
            } else {
                response.sendRedirect("/auth/onboarding");
            }
        } else {
            // 예외 상황 처리 (예: DB에 없으면 로그인 실패로 처리)
            response.sendRedirect("/auth/login?error=true");
        }
    }
}
