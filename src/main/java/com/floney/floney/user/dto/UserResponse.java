package com.floney.floney.user.dto;

import com.floney.floney.user.entity.User;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResponse implements Serializable {
    private final Long id;
    private final String nickname;
    private final String email;
    private final String password;
    private final String profileImg;
    private final int marketingAgree;
    private final int subscribe;
    private final LocalDateTime lastAdTime;
    private final String provider;
    private final boolean status;

    public static UserResponse from(User entity) {
        return new UserResponseBuilder()
                .id(entity.getId())
                .nickname(entity.getNickname())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .profileImg(entity.getProfileImg())
                .marketingAgree(entity.getMarketingAgree())
                .subscribe(entity.getSubscribe())
                .lastAdTime(entity.getLastAdTime())
                .provider(entity.getProvider())
                .status(entity.isStatus())
                .build();
    }

    public User to() {
        return User.builder()
                .nickname(nickname)
                .email(email)
                .password(password)
                .profileImg(profileImg)
                .marketingAgree(marketingAgree)
                .subscribe(subscribe)
                .lastAdTime(lastAdTime)
                .provider(provider)
                .status(status)
                .build();
    }
}