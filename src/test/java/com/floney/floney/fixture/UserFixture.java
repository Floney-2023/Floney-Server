package com.floney.floney.fixture;

import com.floney.floney.user.dto.constant.Provider;
import com.floney.floney.user.entity.User;

import java.time.LocalDateTime;

import static com.floney.floney.fixture.BookFixture.EMAIL;

public class UserFixture {

    public static User emailUser() {
        return User.builder()
                .nickname("floney")
                .email(EMAIL)
                .password("password")
                .profileImg("img")
                .lastAdTime(LocalDateTime.of(2023, 4, 15, 12, 30, 45))
                .provider(Provider.EMAIL)
                .receiveMarketing(true)
                .build();
    }

    public static User kakaoUser() {
        return User.builder()
                .nickname("floney")
                .email("floney2@naver.com")
                .password("password")
                .profileImg("img")
                .lastAdTime(LocalDateTime.of(2023, 4, 15, 12, 30, 45))
                .provider(Provider.KAKAO)
                .receiveMarketing(false)
                .build();
    }

}
