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
        return attributes.get("id").toString();
    }

    @Override
    public String getEmail() {
        Object accountObj = attributes.get("kakao_account");
        if (accountObj instanceof Map account) {
            return account.get("email") != null ? account.get("email").toString() : null;
        }
        return null;
    }

    @Override
    public String getName() {
        Object accountObj = attributes.get("kakao_account");
        if (accountObj instanceof Map account) {
            Object profileObj = account.get("profile");
            if (profileObj instanceof Map profile) {
                return profile.get("nickname") != null ? profile.get("nickname").toString() : null;
            }
        }

        Object propertiesObj = attributes.get("properties");
        if (propertiesObj instanceof Map props) {
            return props.get("nickname") != null ? props.get("nickname").toString() : null;
        }

        return null;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
