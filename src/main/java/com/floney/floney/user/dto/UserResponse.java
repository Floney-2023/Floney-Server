package com.floney.floney.user.dto;

import com.floney.floney.user.dto.constant.Provider;
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
    private final String nickname;
    private final String email;
    private final String password;
    private final String profileImg;
    private final boolean marketingAgree;
    private final boolean subscribe;
    private final LocalDateTime lastAdTime;
    private final Provider provider;
    private final boolean status;

    public static UserResponse from(User entity) {
        return new UserResponseBuilder()
                .nickname(entity.getNickname())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .profileImg(entity.getProfileImg())
                .marketingAgree(entity.isMarketingAgree())
                .subscribe(entity.isSubscribe())
                .lastAdTime(entity.getLastAdTime())
                .provider(Provider.findByName(entity.getProvider()))
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