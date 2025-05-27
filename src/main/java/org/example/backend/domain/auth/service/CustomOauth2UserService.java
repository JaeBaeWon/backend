package org.example.backend.domain.auth.service;

import org.example.backend.domain.auth.config.*;
import org.example.backend.domain.user.entity.User;
import org.example.backend.domain.user.entity.UserRole;
import org.example.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("소셜 로그인 유저 정보: {}", oAuth2User.getAttributes());

        String provider = userRequest.getClientRegistration().getRegistrationId();

        OAuth2UserInfo oAuth2UserInfo = null;

        // ✅ 소셜 제공자별 분기
        if ("google".equals(provider)) {
            log.info("구글 로그인");
            oAuth2UserInfo = new GoogleUserDetails(oAuth2User.getAttributes());
        } else if ("kakao".equals(provider)) {
            log.info("카카오 로그인");
            oAuth2UserInfo = new KakaoUserDetails(oAuth2User.getAttributes());
        } else if ("naver".equals(provider)) {
            log.info("네이버 로그인");
            oAuth2UserInfo = new NaverUserDetails(oAuth2User.getAttributes());
        } else {
            throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인입니다: " + provider);
        }

        String providerId = oAuth2UserInfo.getProviderId();
        String email = provider + "_" + providerId;
        String user_name = oAuth2UserInfo.getName();

        // 기존 회원 여부 확인
        User findUser = userRepository.findByEmail(email).orElse(null);
        User user;

        if (findUser == null) {
            user = User.builder()
                    .email(email)
                    .username(user_name)
                    .provider(provider)
                    .providerId(providerId)
                    .password(UUID.randomUUID().toString()) // ✅ null 방지용 임의 문자열 설정
                    .role(UserRole.CONSUMER)
                    .build();

            userRepository.save(user);
            log.info("신규 소셜 회원 저장: {}", email);
        } else {
            user = findUser;
            log.info("기존 회원 로그인: {}", email);
        }

        return new CustomOauth2UserDetails(user, oAuth2User.getAttributes());
    }
}
