package org.example.backend.domain.auth.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.domain.auth.util.JWTUtil;
import org.example.backend.domain.user.entity.User;
import org.example.backend.domain.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
<<<<<<< HEAD
    private final UserRepository userRepository; // User 정보를 가져오기 위한 repository
    private final RefreshTokenRepository refreshTokenRepository; // RefreshToken을 DB에 저장할 repository
=======
    private final UserRepository userRepository;
    private final RestTemplate restTemplate; // RestTemplate 추가
>>>>>>> ff3bdc95ac60279c2733f3798d1302096ae7ee95

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        // OAuth2User로부터 이메일과 역할 정보 추출
        CustomOauth2UserDetails customUser = (CustomOauth2UserDetails) authentication.getPrincipal();
        String email = customUser.getUsername();
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

<<<<<<< HEAD
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
=======
        // ✅ AccessToken, RefreshToken 쿠키 전달
        Cookie accessCookie = new Cookie("accessToken", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(60 * 30); // 30분

        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(60 * 60 * 24 * 14); // 14일
>>>>>>> ff3bdc95ac60279c2733f3798d1302096ae7ee95

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());

        // ✅ RefreshToken을 DB에 저장 (새로 생성한 refreshToken)
        RefreshToken refreshTokenEntity = new RefreshToken(user, refreshToken, LocalDateTime.now());
        refreshTokenRepository.save(refreshTokenEntity);

        // 액세스 토큰을 사용해 사용자 정보 가져오기
        String provider = customUser.getProvider();
        String tokenUri = getTokenUri(provider);
        String userInfoUri = getUserInfoUri(provider);

        if (tokenUri != null && userInfoUri != null) {
            // 액세스 토큰을 사용하여 사용자 정보 요청
            String accessTokenFromProvider = getAccessTokenFromProvider(tokenUri);
            OAuth2User oAuth2User = getUserInfoFromProvider(userInfoUri, accessTokenFromProvider);

            // 사용자 정보를 처리하여 필요한 작업을 수행할 수 있습니다
            log.info("사용자 정보: {}", oAuth2User.getAttributes());
        }

        // ✅ 리디렉션 URL 생성 (온보딩 여부는 필터에서 처리)
        String redirectUrl = UriComponentsBuilder
                .fromUriString("https://podopicker.store/oauth-redirect")
                .queryParam("onboardingComplete", user.isOnboardingCompleted()) // 온보딩 여부를 쿼리 파라미터로 전달
                .build()
                .toUriString();

        log.info("🔁 OAuth2 리디렉션 → {}", redirectUrl);
        response.sendRedirect(redirectUrl);
    }

    // 토큰 발급을 위한 URI를 반환
    private String getTokenUri(String provider) {
        if ("google".equals(provider)) {
            return "https://oauth2.googleapis.com/token";
        } else if ("kakao".equals(provider)) {
            return "https://kauth.kakao.com/oauth/token";
        } else if ("naver".equals(provider)) {
            return "https://nid.naver.com/oauth2.0/token";
        }
        return null;
    }

    // 사용자 정보를 가져오기 위한 URI를 반환
    private String getUserInfoUri(String provider) {
        if ("google".equals(provider)) {
            return "https://www.googleapis.com/oauth2/v3/userinfo";
        } else if ("kakao".equals(provider)) {
            return "https://kapi.kakao.com/v2/user/me";
        } else if ("naver".equals(provider)) {
            return "https://openapi.naver.com/v1/nid/me";
        }
        return null;
    }

    // 제공자의 토큰 URI에서 액세스 토큰을 가져오는 메서드
    private String getAccessTokenFromProvider(String tokenUri) {
        // 실제로는 REST API를 통해 토큰을 요청합니다.
        // 예시: restTemplate.postForObject(tokenUri, params, AccessTokenResponse.class);
        return "your_access_token_here"; // 실제로는 액세스 토큰을 반환해야 합니다
    }

    // 제공자의 사용자 정보를 가져오는 메서드
    private OAuth2User getUserInfoFromProvider(String userInfoUri, String accessToken) {
        // 실제로는 REST API를 통해 사용자 정보를 요청합니다.
        // 예시: restTemplate.getForObject(userInfoUri + "?access_token=" + accessToken,
        // OAuth2User.class);
        return new CustomOauth2UserDetails(null, null); // 실제로는 유저 정보를 반환해야 합니다
    }
}
