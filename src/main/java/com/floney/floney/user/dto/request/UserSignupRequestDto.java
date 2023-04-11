package com.floney.floney.user.dto.request;

import com.floney.floney.user.dto.UserDto;
import com.floney.floney.user.dto.constant.Provider;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserSignupRequestDto {
    private final String email;
    private final String nickname;
    private final String password;
    private final int marketingAgree;

    public UserDto to() {
        return UserDto.of(
                nickname,
                email,
                password,
                null,
                marketingAgree,
                0,
                null,
                Provider.EMAIL.getName()
        );
    }
}
