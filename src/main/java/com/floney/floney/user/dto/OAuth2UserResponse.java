package com.floney.floney.user.dto;

import java.util.Map;

public interface OAuth2UserResponse {
    Map<String, Object> getAttributes();
    String getProviderId();
    String getProvider();
    String getEmail();
    String getNickname();
    UserResponse toUserDto();
}
