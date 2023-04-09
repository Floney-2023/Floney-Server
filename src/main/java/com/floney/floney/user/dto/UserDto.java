package com.floney.floney.user.dto;

import com.floney.floney.user.entity.User;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class UserDto implements Serializable {
    private final String nickname;
    private final String email;
    private final String password;
    private final String profileImg;
    private final int marketingAgree;
    private final int subscribe;
    private final LocalDateTime lastAdTime;
    private final String provider;

    public static UserDto of(String nickname, String email, String password, String profileImg,
                             int marketingAgree, int subscribe, LocalDateTime lastAdTime, String provider) {
        return new UserDto(
                nickname, email, password, profileImg, marketingAgree, subscribe, lastAdTime, provider
        );
    }

    public static UserDto from(User entity) {
        return new UserDto(
                entity.getNickname(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getProfileImg(),
                entity.getMarketingAgree(),
                entity.getSubscribe(),
                entity.getLastAdTime(),
                entity.getProvider()
        );
    }

    public User to() {
        return User.of(
                nickname,
                email,
                password,
                profileImg,
                marketingAgree,
                subscribe,
                lastAdTime,
                provider
        );
    }
}