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
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate; // RestTemplate을 주입받아 사용
    private final String clientBaseUrl = "https://podopicker.store"; // 리디렉션 기본 URL

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        CustomOauth2UserDetails customUser = (CustomOauth2UserDetails) authentication.getPrincipal();
        String email = customUser.getUsername();
        log.info("✅ OAuth2 로그인 성공: {}", email);

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            log.warn("❌ OAuth 로그인 성공했지만 사용자 DB에 없음: {}", email);
            response.sendRedirect("/auth/login?error=true");
            return;
        }

        String accessToken = jwtUtil.createAccessToken(user.getUserId(), email, user.getRole().name());
        String refreshToken = jwtUtil.createRefreshToken(email);

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(60 * 30)
                .sameSite("None")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(60 * 60 * 24 * 14)
                .sameSite("None")
                .build();

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());

        RefreshToken refreshTokenEntity = new RefreshToken(user.getEmail(), refreshToken);
        refreshTokenRepository.save(refreshTokenEntity);

        String provider = customUser.getProvider();
        String tokenUri = getTokenUri(provider);
        String userInfoUri = getUserInfoUri(provider);

        if (tokenUri != null && userInfoUri != null) {
            String accessTokenFromProvider = getAccessTokenFromProvider(tokenUri);
            OAuth2User oAuth2User = getUserInfoFromProvider(userInfoUri, accessTokenFromProvider);
            log.info("사용자 정보: {}", oAuth2User.getAttributes());
        }

        String redirectUrl = UriComponentsBuilder
                .fromUriString(clientBaseUrl + "/oauth-redirect")
                .queryParam("onboardingComplete", user.isOnboardingCompleted())
                .build()
                .toUriString();

        log.info("🔁 OAuth2 리디렉션 → {}", redirectUrl);
        response.sendRedirect(redirectUrl);
    }

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

    private String getAccessTokenFromProvider(String tokenUri) {
        return restTemplate.postForObject(tokenUri, null, String.class);
    }

    private OAuth2User getUserInfoFromProvider(String userInfoUri, String accessToken) {
        return restTemplate.getForObject(userInfoUri + "?access_token=" + accessToken, OAuth2User.class);
    }
}
