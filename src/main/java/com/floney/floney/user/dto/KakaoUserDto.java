package com.floney.floney.user.dto;

import com.floney.floney.user.dto.constant.Provider;
import com.floney.floney.user.entity.User;
import java.util.Map;

public class KakaoUserDto implements OAuth2UserDto {
    private final Map<String, Object> attributes;
    private final Map<String, Object> attributesAccount;
    private final Map<String, Object> attributesProfile;

    private KakaoUserDto(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.attributesAccount = (Map<String, Object>) attributes.get("kakao_account");
        this.attributesProfile = (Map<String, Object>) attributesAccount.get("profile");
    }

    public static KakaoUserDto of(Map<String, Object> attributes) {
        return new KakaoUserDto(attributes);
    }

    @Override
    public UserDto toUserDto() {
        return UserDto.of(getNickname(), getEmail(), null, null, 0, 0, null, getProvider());
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
