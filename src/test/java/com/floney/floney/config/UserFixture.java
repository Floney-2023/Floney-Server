package com.floney.floney.config;

import com.floney.floney.user.dto.constant.Provider;
import com.floney.floney.user.entity.User;

import java.time.LocalDateTime;

import static com.floney.floney.book.BookFixture.EMAIL;

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
                .marketingAgree(true)
                .lastAdTime(LocalDateTime.of(2023, 4, 15, 12, 30, 45))
                .provider(Provider.EMAIL.getName())
                .build();
    }
}
