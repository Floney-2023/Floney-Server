package com.floney.floney.User;

import com.floney.floney.user.entity.User;

import java.time.LocalDateTime;

import static com.floney.floney.book.BookFixture.EMAIL;

public class UserFixture {

    public static User createUser() {
        return
            User.of("floney", EMAIL, "1234", "ss", 0, 0, LocalDateTime.of(2023, 4, 15, 12, 30, 45), "f");

    }
}
