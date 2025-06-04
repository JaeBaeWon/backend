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

    // 생성자에서 DefaultOAuth2AuthorizationRequestResolver를 위임합니다.
    public CustomAuthorizationRequestResolver(ClientRegistrationRepository repo, String baseUri) {
        this.delegate = new DefaultOAuth2AuthorizationRequestResolver(repo, baseUri);
    }

    // 기본 resolve 메서드 구현 (HttpServletRequest만 받는 메서드)
    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        // 기본 OAuth2AuthorizationRequest를 커스터마이즈하여 반환
        return customize(delegate.resolve(request));
    }

    // 클라이언트 등록 ID를 사용하는 resolve 메서드 구현
    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        // 기본 OAuth2AuthorizationRequest를 커스터마이즈하여 반환
        return customize(delegate.resolve(request, clientRegistrationId));
    }

    // OAuth2AuthorizationRequest를 커스터마이즈하는 메서드
    private OAuth2AuthorizationRequest customize(OAuth2AuthorizationRequest request) {
        if (request == null)
            return null;

        // 기존 파라미터에 추가적인 파라미터를 넣기 위해 새로운 Map 생성
        Map<String, Object> additionalParams = new HashMap<>(request.getAdditionalParameters());
        // prompt 파라미터를 "select_account"로 설정하여 계정 선택 창을 항상 띄움
        additionalParams.put("prompt", "select_account");

        // 커스터마이즈된 OAuth2AuthorizationRequest 반환
        return OAuth2AuthorizationRequest.from(request)
                .additionalParameters(additionalParams)
                .build();
    }
}
