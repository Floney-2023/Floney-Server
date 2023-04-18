package com.floney.floney.user.dto.request;

import com.floney.floney.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserSignupRequestDto {
    private String email;
    private String nickname;
    private String password;
    private int marketingAgree;

    private UserSignupRequestDto(String email, String nickname, String password, int marketingAgree) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.marketingAgree = marketingAgree;
    }

    public UserDto to() {
        return UserDto.of(
            nickname,
            email,
            password,
            null,
            marketingAgree,
            0,
            null,
            "email"
        );
    }
}
