package com.floney.floney.user.dto;

import java.util.Map;

public interface OAuth2UserDto {
    Map<String, Object> getAttributes();
    String getProviderId();
    String getProvider();
    String getEmail();
    String getNickname();
    UserDto toUserDto();
}
