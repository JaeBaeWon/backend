package org.example.backend.domain.auth.config;

import jakarta.servlet.ServletException;
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
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository; // User 정보를 가져오기 위한 repository
    private final RefreshTokenRepository refreshTokenRepository; // RefreshToken을 DB에 저장할 repository

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        // OAuth2User로부터 이메일과 역할 정보 추출
        CustomOauth2UserDetails customUser = (CustomOauth2UserDetails) authentication.getPrincipal();
        String email = customUser.getUsername(); // 예: google_12345
        log.info("✅ OAuth2 로그인 성공: {}", email);

        // 사용자 DB에서 이메일로 사용자 정보 검색
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            log.warn("❌ OAuth 로그인 성공했지만 사용자 DB에 없음: {}", email);
            response.sendRedirect("/auth/login?error=true");
            return;
        }

        // ✅ JWT 발급
        String accessToken = jwtUtil.createAccessToken(user.getUserId(), email, user.getRole().name());
        String refreshToken = jwtUtil.createRefreshToken(email);

        // ✅ AccessToken 쿠키 생성
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(60 * 30) // 30분
                .sameSite("None") // SameSite 설정
                .build();

        // ✅ RefreshToken 쿠키 생성
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(60 * 60 * 24 * 14) // 14일
                .sameSite("None") // SameSite 설정
                .build();

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());

        // ✅ RefreshToken을 DB에 저장 (새로 생성한 refreshToken)
        RefreshToken refreshTokenEntity = new RefreshToken(user, refreshToken, LocalDateTime.now());
        refreshTokenRepository.save(refreshTokenEntity);

        // ✅ 리디렉션 URL 생성 (온보딩 여부는 필터에서 처리)
        String redirectUrl = UriComponentsBuilder
                .fromUriString("https://podopicker.store/oauth-redirect")
                .queryParam("onboardingComplete", user.isOnboardingCompleted()) // 온보딩 여부를 쿼리 파라미터로 전달
                .build()
                .toUriString();

        log.info("🔁 OAuth2 리디렉션 → {}", redirectUrl);
        response.sendRedirect(redirectUrl);
    }
}
