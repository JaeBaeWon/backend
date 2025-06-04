package org.example.backend.domain.auth.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.domain.auth.util.JWTUtil;
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
    private final JWTUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        CustomOauth2UserDetails customUser = (CustomOauth2UserDetails) authentication.getPrincipal();
        String email = customUser.getUsername(); // ex) "kakao_12345"
        log.info("✅ OAuth2 로그인 성공: {}", email);

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            log.warn("❌ OAuth 로그인 성공했지만 사용자 DB에 없음: {}", email);
            response.sendRedirect("/auth/login?error=true");
            return;
        }

        // ✅ JWT 생성 후 쿠키로 설정
        String accessToken = jwtUtil.createAccessToken(user.getUserId(), user.getEmail(), user.getRole().name());

        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true); // HTTPS일 경우
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(60 * 30); // 30분

        response.addCookie(accessTokenCookie);

        // ✅ 온보딩 여부에 따라 리디렉션
        if (user.isOnboardingCompleted()) {
            log.info("🔁 온보딩 완료 → 메인으로 리디렉션");
            response.sendRedirect("/");
        } else {
            log.info("🧾 온보딩 미완료 → 온보딩 페이지로 리디렉션");
            response.sendRedirect("/auth/onboarding");
        }
    }
}
