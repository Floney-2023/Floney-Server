package com.floney.floney.user.dto.response;

import com.floney.floney.common.constant.Status;
import com.floney.floney.user.dto.constant.Provider;
import com.floney.floney.user.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResponse {
    private final String nickname;
    private final String email;
    private final String password;
    private final String profileImg;
    private final boolean subscribe;
    private final LocalDateTime lastAdTime;
    private final Provider provider;
    private final Status status;

    public static UserResponse from(User entity) {
        return new UserResponseBuilder()
                .nickname(entity.getNickname())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .profileImg(entity.getProfileImg())
                .subscribe(entity.isSubscribe())
                .lastAdTime(entity.getLastAdTime())
                .provider(Provider.findByName(entity.getProvider()))
                .status(entity.getStatus())
                .build();
    }

    public User to() {
        return User.builder()
                .nickname(nickname)
                .email(email)
                .password(password)
                .profileImg(profileImg)
                .subscribe(subscribe)
                .lastAdTime(lastAdTime)
                .provider(provider.getName())
                .build();
    }
}
