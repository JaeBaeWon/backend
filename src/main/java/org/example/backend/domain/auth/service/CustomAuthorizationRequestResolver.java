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
        return customize(delegate.resolve(request)); // 기본 OAuth2AuthorizationRequest를 커스터마이즈하여 반환
    }

    // 클라이언트 등록 ID를 사용하는 resolve 메서드 구현
    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        return customize(delegate.resolve(request, clientRegistrationId)); // 기본 OAuth2AuthorizationRequest를 커스터마이즈하여 반환
    }

    // OAuth2AuthorizationRequest를 커스터마이즈하는 메서드
    private OAuth2AuthorizationRequest customize(OAuth2AuthorizationRequest request) {
        if (request == null)
            return null; // null 체크

        // 기존 파라미터에 추가적인 파라미터를 넣기 위해 새로운 Map 생성
        Map<String, Object> additionalParams = new HashMap<>(request.getAdditionalParameters());
        additionalParams.put("prompt", "select_account"); // 항상 계정 선택 창 띄우기

        // 'state' 파라미터를 확인하고 비어 있다면 기본값을 설정
        if (!additionalParams.containsKey("state") || additionalParams.get("state") == null) {
            additionalParams.put("state", generateState()); // 새로운 'state' 값을 생성하여 추가
        }

        // 커스터마이즈된 OAuth2AuthorizationRequest 반환
        return OAuth2AuthorizationRequest.from(request)
                .additionalParameters(additionalParams)
                .build();
    }

    // 'state' 파라미터 값을 생성하는 메서드
    private String generateState() {
        // 여기에 state 값을 생성하는 로직을 추가합니다. 예시로 UUID를 사용할 수 있습니다.
        return java.util.UUID.randomUUID().toString();
    }
}
