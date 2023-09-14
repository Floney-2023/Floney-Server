package com.floney.floney.fixture;

import static com.floney.floney.fixture.BookFixture.EMAIL;

import com.floney.floney.user.dto.constant.Provider;
import com.floney.floney.user.entity.User;
import java.time.LocalDateTime;

public class UserFixture {

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
            .build();
    }

    public static User createUser2() {
        return User.builder()
            .nickname("floney")
            .email("floney2@naver.com")
            .password("1234")
            .profileImg("img")
            .subscribe(false)
            .lastAdTime(LocalDateTime.of(2023, 4, 15, 12, 30, 45))
            .provider(Provider.EMAIL)
            .build();
    }

}
