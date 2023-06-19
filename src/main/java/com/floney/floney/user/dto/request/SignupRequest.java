package com.floney.floney.user.dto.request;

import com.floney.floney.user.dto.constant.Provider;
import com.floney.floney.user.entity.User;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SignupRequest {

    @NotNull private String email;
    @NotNull private String nickname;
    @NotNull private String password;

    public LoginRequest toLoginRequest() {
        return LoginRequest.builder()
                .email(email)
                .password(password)
                .build();
    }

    public User to() {
        return User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .provider(Provider.EMAIL.getName())
                .build();
    }

    public User to(String provider, String providerId) {
        return User.builder()
                .email(email)
                .password("auth")
                .nickname(nickname)
                .provider(provider)
                .providerId(providerId)
                .build();
    }

}
