package com.floney.floney.user.dto.response;

import com.floney.floney.book.dto.process.MyBookInfo;
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
    private final boolean subscribe;
    private final LocalDateTime lastAdTime;
    private final List<MyBookInfo> myBooks;

    public static MyPageResponse from(UserResponse userResponse, List<MyBookInfo> myBooks) {
        return MyPageResponse.builder()
                .nickname(userResponse.getNickname())
                .email(userResponse.getEmail())
                .subscribe(userResponse.isSubscribe())
                .lastAdTime(userResponse.getLastAdTime())
                .myBooks(myBooks)
                .build();
    }
}
