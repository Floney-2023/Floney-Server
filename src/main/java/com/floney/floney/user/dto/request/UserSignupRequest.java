package com.floney.floney.user.dto.request;

import com.floney.floney.user.dto.UserResponse;
import com.floney.floney.user.dto.constant.Provider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserSignupRequest {
    private String email;
    private String nickname;
    private String password;
    private int marketingAgree;

    public UserResponse to() {
        return UserResponse.builder()
                .nickname(nickname)
                .email(email)
                .password(password)
                .marketingAgree(marketingAgree)
                .subscribe(0)
                .status(true)
                .provider(Provider.EMAIL.getName())
                .build();
    }
}
