package com.mysite.sbb.user;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private UserService userService;

    @Autowired
    public CustomOAuth2UserService(@Lazy UserService userService) {
        this.userService = userService;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // 로그인 제공자 확인 (네이버, 카카오, 구글 등)
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String username = null;
        String email = null;

        // 로그인 제공자별 사용자 정보 추출
        if ("naver".equals(registrationId)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            username = (String) response.get("nickname");
            email = (String) response.get("email");
        } else if ("kakao".equals(registrationId)) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            username = (String) profile.get("nickname");
            email = (String) kakaoAccount.get("email");
        } else if ("google".equals(registrationId)) {
            username = (String) attributes.get("name");
            email = (String) attributes.get("email");
        }

        // 사용자 정보를 DB에 저장하거나 가져옵니다.
        SiteUser user = userService.getUser(username);
        if (user == null) {
            user = new SiteUser();
            user.setUsername(username);
            user.setEmail(email);
            userService.create(user.getUsername(), user.getEmail(), "");
        }

        // 사용자 정보 반환을 위한 customAttributes 설정
        Map<String, Object> customAttributes = Map.of(
                "username", username,
                "name", username);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                customAttributes, // 사용자 정보가 담긴 attributes 맵
                "username" // 기본 식별자로 사용할 필드
        );
    }
}