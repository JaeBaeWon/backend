package org.example.backend.domain.auth.config;

import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class KakaoUserDetails implements OAuth2UserInfo {

    private Map<String, Object> attributes;

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return attributes.get("id") != null ? attributes.get("id").toString() : null;
    }

    @Override
    public String getEmail() {
        // kakao_account이 Map으로 존재하고 email이 그 안에 있을 경우 안전하게 처리
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        return kakaoAccount != null ? (String) kakaoAccount.get("email") : null;
    }

    @Override
    public String getName() {
        // properties가 Map으로 존재하고 nickname이 그 안에 있을 경우 안전하게 처리
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        return properties != null ? (String) properties.get("nickname") : null;
    }
}
