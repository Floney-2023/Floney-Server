package com.floney.floney.fixture;

import com.floney.floney.user.entity.User;

import java.time.LocalDateTime;

import static com.floney.floney.user.dto.constant.Provider.EMAIL;
import static com.floney.floney.user.dto.constant.Provider.KAKAO;

public class UserFixture {

    public static final String DEFAULT_EMAIL = "floney@email.com";
    public static final String DEFAULT_PASSWORD = "password";
    public static final String DEFAULT_NICKNAME = "nickname";

    public static User emailUser() {
        return User.builder()
            .nickname(DEFAULT_NICKNAME)
            .email(DEFAULT_EMAIL)
            .password(DEFAULT_PASSWORD)
            .lastAdTime(LocalDateTime.now())
            .provider(EMAIL)
            .receiveMarketing(true)
            .build();
    }

    public static User emailUserWithEmail(final String email) {
        return User.builder()
            .nickname(DEFAULT_NICKNAME)
            .email(email)
            .password(DEFAULT_PASSWORD)
            .lastAdTime(LocalDateTime.now())
            .provider(EMAIL)
            .receiveMarketing(true)
            .build();
    }

    public static User kakaoUser() {
        return User.builder()
            .nickname(DEFAULT_NICKNAME)
            .email("kakao@email.com")
            .password(DEFAULT_PASSWORD)
            .lastAdTime(LocalDateTime.now())
            .provider(KAKAO)
            .receiveMarketing(false)
            .build();
    }

}
