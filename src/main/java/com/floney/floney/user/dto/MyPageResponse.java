package com.floney.floney.user.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

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
    // TODO: 해당 유저의 가계부 정보(가계부 이름, 가계부 인원) 추가

    public static MyPageResponse from(UserResponse userResponse) {
        return MyPageResponse.builder()
                .nickname(userResponse.getNickname())
                .email(userResponse.getEmail())
                .subscribe(userResponse.getSubscribe())
                .lastAdTime(userResponse.getLastAdTime())
                .build();
    }
}
