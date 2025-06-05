package org.example.backend.domain.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.domain.auth.entity.RefreshToken;
import org.example.backend.domain.auth.repository.RefreshTokenRepository;
import org.example.backend.domain.auth.util.JWTUtil;
import org.example.backend.domain.user.entity.User;
import org.example.backend.domain.user.repository.UserRepository;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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
    private final OAuthProviderInfoResolver providerResolver;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String clientBaseUrl = "https://app.podopicker.store";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        CustomOauth2UserDetails customUser = (CustomOauth2UserDetails) authentication.getPrincipal();
        String email = customUser.getUsername();
        String provider = customUser.getProvider();
        String code = customUser.getAuthorizationCode();
        log.info("✅ OAuth2 로그인 성공: {}", email);

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            log.warn("❌ OAuth 로그인 성공했지만 사용자 DB에 없음: {}", email);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found");
            return;
        }

        // JWT 발급
        String accessToken = jwtUtil.createAccessToken(user.getUserId(), email, user.getRole().name());
        String refreshToken = jwtUtil.createRefreshToken(email);

        response.addHeader("Set-Cookie", ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true).secure(true).path("/").maxAge(1800).sameSite("None").build().toString());

        response.addHeader("Set-Cookie", ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true).secure(true).path("/").maxAge(60 * 60 * 24 * 14).sameSite("None").build().toString());

        refreshTokenRepository.save(new RefreshToken(email, refreshToken));

        // ✅ OAuth Provider 정보로 동적 토큰 요청
        String tokenUri = providerResolver.getTokenUri(provider);
        String userInfoUri = providerResolver.getUserInfoUri(provider);
        String clientId = providerResolver.getClientId(provider);
        String clientSecret = providerResolver.getClientSecret(provider);
        String redirectUri = providerResolver.getRedirectUri(provider);

        String accessTokenFromProvider = getAccessTokenFromProvider(tokenUri, code, clientId, clientSecret, redirectUri);
        OAuth2User oAuth2User = getUserInfoFromProvider(userInfoUri, accessTokenFromProvider);


        /*String accessTokenFromProvider = getAccessTokenFromProvider(tokenUri, code, clientId, clientSecret, redirectUri);
        OAuth2User oAuth2User = getUserInfoFromProvider(userInfoUri, accessTokenFromProvider);
*/
        log.info("📥 사용자 정보: {}", oAuth2User.getAttributes());


        /*ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
>>>>>>> Stashed changes
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
        refreshTokenRepository.save(refreshTokenEntity);*/

        /*String provider = customUser.getProvider();
        String tokenUri = getTokenUri(provider);
        String userInfoUri = getUserInfoUri(provider);*/

        /*if (tokenUri != null && userInfoUri != null) {
            String accessTokenFromProvider = getAccessTokenFromProvider(tokenUri, customUser.getAuthorizationCode());
            OAuth2User oAuth2User = getUserInfoFromProvider(userInfoUri, accessTokenFromProvider);
            log.info("사용자 정보: {}", oAuth2User.getAttributes());
        }*/

        // ✅ 프론트로 onboarding 여부 포함 redirect
        String redirectUrl = UriComponentsBuilder
                .fromUriString(clientBaseUrl + "/oauth-redirect")
                .queryParam("onboardingComplete", user.isOnboardingCompleted())
                .build()
                .toUriString();

        // ✅ JSON 응답으로 리디렉션 URL 전달
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> responseBody = Map.of("redirectUrl", redirectUrl);
        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }

//    /*private String getTokenUri(String provider) {
//        if ("google".equals(provider)) {
//            return "https://oauth2.googleapis.com/token";
//        } else if ("kakao".equals(provider)) {
//            return "https://kauth.kakao.com/oauth/token";
//        } else if ("naver".equals(provider)) {
//            return "https://nid.naver.com/oauth2.0/token";
//        }
//        return null;
//    }
//
//    private String getUserInfoUri(String provider) {
//        if ("google".equals(provider)) {
//            return "https://www.googleapis.com/oauth2/v3/userinfo";
//        } else if ("kakao".equals(provider)) {
//            return "https://kapi.kakao.com/v2/user/me";
//        } else if ("naver".equals(provider)) {
//            return "https://openapi.naver.com/v1/nid/me";
//        }
//        return null;
//    }*/

    private String getAccessTokenFromProvider(String tokenUri, String code, String clientId, String clientSecret, String redirectUri) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUri, request, Map.class);

        return (String) response.getBody().get("access_token");
    }

    /*private String getAccessTokenFromProvider(String tokenUri, String authorizationCode) {
        // 요청 파라미터로 전달된 authorizationCode를 이용해 액세스 토큰을 요청합니다.
        // 예를 들어, RestTemplate을 사용하여 POST 요청을 보내고 액세스 토큰을 얻습니다.

        // 필요한 파라미터를 설정하고 요청을 보냅니다.
        Map<String, String> params = new HashMap<>();
        params.put("code", authorizationCode);
        params.put("client_id", "your-client-id");
        params.put("client_secret", "your-client-secret");
        params.put("redirect_uri", "your-redirect-uri");

        return restTemplate.postForObject(tokenUri, params, String.class);
    }*/

    private OAuth2User getUserInfoFromProvider(String userInfoUri, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(userInfoUri, HttpMethod.GET, entity, Map.class);
        return new DefaultOAuth2User(List.of(), response.getBody(), "id");
    }

    /*private OAuth2User getUserInfoFromProvider(String userInfoUri, String accessToken) {
        return restTemplate.getForObject(userInfoUri + "?access_token=" + accessToken, OAuth2User.class);
    }*/
}
