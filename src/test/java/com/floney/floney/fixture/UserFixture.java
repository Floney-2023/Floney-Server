package com.floney.floney.fixture;

import com.floney.floney.user.dto.constant.Provider;
import com.floney.floney.user.entity.User;

import java.time.LocalDateTime;

import static com.floney.floney.fixture.BookFixture.EMAIL;

public class UserFixture {

    public static final String DELETE_VALUE = "알수없음";

    private static final User user = createUser();

    public static User getUser() {
        return user;
    }

    public static User createUser() {
        return User.builder()
                .nickname("floney")
                .email(EMAIL)
                .password("1234")
                .profileImg("img")
                .subscribe(false)
                .lastAdTime(LocalDateTime.of(2023, 4, 15, 12, 30, 45))
                .provider(Provider.EMAIL)
                .receiveMarketing(true)
                .build();
    }

    public static User createSubscribeUser() {
        return User.builder()
                .nickname("floney")
                .email(EMAIL)
                .password("1234")
                .profileImg("img")
                .subscribe(true)
                .lastAdTime(LocalDateTime.of(2023, 4, 15, 12, 30, 45))
                .provider(Provider.EMAIL)
                .receiveMarketing(true)
                .build();
    }

    public static User createKakaoUser() {
        return User.builder()
                .nickname("floney")
                .email("floney2@naver.com")
                .password("1234")
                .profileImg("img")
                .subscribe(false)
                .lastAdTime(LocalDateTime.of(2023, 4, 15, 12, 30, 45))
                .provider(Provider.KAKAO)
                .receiveMarketing(false)
                .build();
    }

}
