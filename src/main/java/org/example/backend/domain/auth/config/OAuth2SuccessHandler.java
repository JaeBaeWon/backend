package org.example.backend.domain.auth.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.domain.auth.entity.RefreshToken;
import org.example.backend.domain.auth.repository.RefreshTokenRepository;
import org.example.backend.domain.auth.util.JWTUtil;
import org.example.backend.domain.user.entity.User;
import org.example.backend.domain.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        CustomOauth2UserDetails customUser = (CustomOauth2UserDetails) authentication.getPrincipal();
        String email = customUser.getUsername(); // 예: google_12345
        log.info("✅ OAuth2 로그인 성공: {}", email);

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            log.warn("❌ OAuth 로그인 성공했지만 사용자 DB에 없음: {}", email);
            response.sendRedirect("/auth/login?error=true");
            return;
        }

        // ✅ Access Token 발급
        String accessToken = jwtUtil.createAccessToken(user.getUserId(), email, user.getRole().name());

        // ✅ Refresh Token 발급
        String refreshToken = jwtUtil.createRefreshToken(email);

        // ✅ Refresh Token DB 저장 또는 갱신
        refreshTokenRepository.findByEmail(email).ifPresentOrElse(existing -> {
            existing.setToken(refreshToken);
            existing.setExpiration(LocalDateTime.now().plusDays(14));
            refreshTokenRepository.save(existing);
        }, () -> {
            RefreshToken token = new RefreshToken(null, email, refreshToken, LocalDateTime.now().plusDays(14));
            refreshTokenRepository.save(token);
        });

        // ✅ Refresh Token 쿠키로 전달 (HttpOnly)
        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true); // HTTPS 환경에서만 전달됨
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(60 * 60 * 24 * 14); // 14일

        response.addCookie(refreshCookie);

        // ✅ 리디렉션 URL 생성
        String redirectUrl = UriComponentsBuilder
                .fromUriString("https://podopicker.store/oauth-redirect")
                .queryParam("onboardingComplete", user.isOnboardingCompleted())
                .build()
                .toUriString();

        log.info("🔁 OAuth2 리디렉션 → {}", redirectUrl);

        // ✅ 최종 리디렉션
        response.sendRedirect(redirectUrl);
    }
}
