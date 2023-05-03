package com.floney.floney.user.dto;

import com.floney.floney.user.dto.constant.Provider;
import java.util.Map;

public class KakaoUserResponse implements OAuth2UserResponse {
    private final Map<String, Object> attributes;
    private final Map<String, Object> attributesAccount;
    private final Map<String, Object> attributesProfile;

    private KakaoUserResponse(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.attributesAccount = (Map<String, Object>) attributes.get("kakao_account");
        this.attributesProfile = (Map<String, Object>) attributesAccount.get("profile");
    }

    public static KakaoUserResponse of(Map<String, Object> attributes) {
        return new KakaoUserResponse(attributes);
    }

    @Override
    public UserResponse toUserDto() {
        return UserResponse.builder()
                .nickname(getNickname())
                .email(getEmail())
                .subscribe(0)
                .provider(getProvider())
                .status(true)
                .build();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getProvider() {
        return Provider.KAKAO.getName();
    }

    @Override
    public String getEmail() {
        return attributesAccount.get("email").toString();
    }

    @Override
    public String getNickname() {
        return attributesProfile.get("nickname").toString();
    }

}
