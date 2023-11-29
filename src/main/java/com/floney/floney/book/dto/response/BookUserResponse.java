package com.floney.floney.book.dto.response;

import com.floney.floney.user.entity.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BookUserResponse {

    private final String email;
    private final String nickname;
    private final String profileImg;

    public static BookUserResponse from(User user) {
        return BookUserResponse.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImg(user.getProfileImg())
                .build();
    }
}

