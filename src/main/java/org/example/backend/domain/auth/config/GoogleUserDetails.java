package org.example.backend.domain.auth.config;

import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class GoogleUserDetails implements OAuth2UserInfo {

    private Map<String, Object> attributes;

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("sub"); // Google에서 제공하는 'sub'는 고유 사용자 ID입니다.
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email"); // Google 사용자 이메일
    }

    @Override
    public String getName() {
        return (String) attributes.get("name"); // Google 사용자 이름
    }
}
