package com.floney.floney.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.floney.floney.common.constant.Status;
import com.floney.floney.user.dto.constant.Provider;
import com.floney.floney.user.entity.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@JsonInclude(Include.NON_NULL)
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResponse {

    private final String nickname;
    private final String email;
    private final String profileImg;
    private final LocalDateTime lastAdTime;
    private final Provider provider;
    private final Status status;

    public static UserResponse from(User user) {
        return new UserResponseBuilder()
                .nickname(user.getNickname())
                .email(user.getEmail())
                .profileImg(user.getProfileImg())
                .lastAdTime(user.getLastAdTime())
                .provider(user.getProvider())
                .status(user.getStatus())
                .build();
    }

    public User to() {
        return User.builder()
                .nickname(nickname)
                .email(email)
                .profileImg(profileImg)
                .lastAdTime(lastAdTime)
                .provider(provider)
                .build();
    }
}
