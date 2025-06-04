package org.example.backend.domain.auth.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.domain.auth.util.JWTUtil;
import org.example.backend.domain.user.entity.User;
import org.example.backend.domain.user.repository.UserRepository;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        CustomOauth2UserDetails customUser = (CustomOauth2UserDetails) authentication.getPrincipal();
        String email = customUser.getUsername(); // ex) "kakao_123456"

        log.info("✅ OAuth2 로그인 성공: {}", email);

        User user = userRepository.findByEmail(email).orElse(null);

        if (user != null) {
            // ✅ JWT 생성
            String jwt = jwtUtil.createAccessToken(user.getUserId(), user.getEmail(), user.getRole().name());

            // ✅ 쿠키로 설정
            ResponseCookie cookie = ResponseCookie.from("accessToken", jwt)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(60 * 60) // 1시간
                    .sameSite("Lax")
                    .build();
            response.setHeader("Set-Cookie", cookie.toString());

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
