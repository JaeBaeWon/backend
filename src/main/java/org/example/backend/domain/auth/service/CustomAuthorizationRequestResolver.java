package org.example.backend.domain.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import java.util.HashMap;
import java.util.Map;

public class CustomAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final OAuth2AuthorizationRequestResolver delegate;

    public CustomAuthorizationRequestResolver(ClientRegistrationRepository repo, String baseUri) {
        this.delegate = new DefaultOAuth2AuthorizationRequestResolver(repo, baseUri);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest oauth2Request = delegate.resolve(request);
        return customize(oauth2Request);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest oauth2Request = delegate.resolve(request, clientRegistrationId);
        return customize(oauth2Request);
    }

    private OAuth2AuthorizationRequest customize(OAuth2AuthorizationRequest request) {
        if (request == null) {
            // 기본값으로 OAuth2AuthorizationRequest 생성
            return OAuth2AuthorizationRequest.authorizationCode()
                    .clientId("default") // 기본값 설정
                    .authorizationUri("/oauth2/authorization") // 기본 URI 설정
                    .redirectUri("/login/oauth2/code/naver") // 기본 redirectUri 설정
                    .scope("openid", "profile") // 예시로 scope 설정
                    .build();
        }

        // OAuth2 요청에 추가 파라미터 추가
        Map<String, Object> additionalParams = new HashMap<>(request.getAdditionalParameters());
        additionalParams.put("prompt", "select_account");

        return OAuth2AuthorizationRequest.from(request)
                .additionalParameters(additionalParams)
                .build();
    }
}
