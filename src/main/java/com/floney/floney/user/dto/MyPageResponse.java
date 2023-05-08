package com.floney.floney.user.dto;

import com.floney.floney.book.dto.MyBookInfo;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
@Builder
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MyPageResponse {
    private final String nickname;
    private final String email;
    private final int subscribe;
    private final LocalDateTime lastAdTime;
    private final List<MyBookInfo> myBooks;

    public static MyPageResponse from(UserResponse userResponse, List<MyBookInfo> myBooks) {
        return MyPageResponse.builder()
            .nickname(userResponse.getNickname())
            .email(userResponse.getEmail())
            .subscribe(userResponse.getSubscribe())
            .lastAdTime(userResponse.getLastAdTime())
            .myBooks(myBooks)
            .build();
    }
}
