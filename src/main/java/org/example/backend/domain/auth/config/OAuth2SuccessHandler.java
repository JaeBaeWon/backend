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
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final String clientBaseUrl = "https://podopicker.store";

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

        refreshTokenRepository.save(new RefreshToken(user.getEmail(), refreshToken));

        // 사용자 정보 로깅 (선택적)
        String provider = customUser.getProvider();
        String code = customUser.getAuthorizationCode();
        String tokenUri = getTokenUri(provider);
        String userInfoUri = getUserInfoUri(provider);
        String clientId = getClientId(provider);
        String clientSecret = getClientSecret(provider);
        String redirectUri = "https://app.podopicker.store/login/oauth2/code/" + provider;

        if (tokenUri != null && userInfoUri != null) {
            String providerAccessToken = getAccessTokenFromProvider(tokenUri, code, clientId, clientSecret,
                    redirectUri);
            Map<String, Object> userInfo = getUserInfoFromProvider(userInfoUri, providerAccessToken);
            log.info("🌐 {} 사용자 정보: {}", provider, userInfo);
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
        return switch (provider) {
            case "google" -> "https://oauth2.googleapis.com/token";
            case "kakao" -> "https://kauth.kakao.com/oauth/token";
            case "naver" -> "https://nid.naver.com/oauth2.0/token";
            default -> null;
        };
    }

    private String getUserInfoUri(String provider) {
        return switch (provider) {
            case "google" -> "https://www.googleapis.com/oauth2/v3/userinfo";
            case "kakao" -> "https://kapi.kakao.com/v2/user/me";
            case "naver" -> "https://openapi.naver.com/v1/nid/me";
            default -> null;
        };
    }

    private String getClientId(String provider) {
        return switch (provider) {
            case "google" -> System.getenv("GOOGLE_CLIENT_ID");
            case "kakao" -> System.getenv("KAKAO_CLIENT_ID");
            case "naver" -> System.getenv("NAVER_CLIENT_ID");
            default -> "";
        };
    }

    private String getClientSecret(String provider) {
        return switch (provider) {
            case "google" -> System.getenv("GOOGLE_CLIENT_SECRET");
            case "kakao" -> System.getenv("KAKAO_CLIENT_SECRET");
            case "naver" -> System.getenv("NAVER_CLIENT_SECRET");
            default -> "";
        };
    }

    private String getAccessTokenFromProvider(String tokenUri, String code, String clientId, String clientSecret,
            String redirectUri) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, String> body = new HashMap<>();
        body.put("grant_type", "authorization_code");
        body.put("code", code);
        body.put("client_id", clientId);
        body.put("client_secret", clientSecret);
        body.put("redirect_uri", redirectUri);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUri, request, Map.class);

        return response.getBody() != null ? (String) response.getBody().get("access_token") : null;
    }

    private Map<String, Object> getUserInfoFromProvider(String userInfoUri, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(userInfoUri, HttpMethod.GET, entity, Map.class);

        return response.getBody();
    }
}
