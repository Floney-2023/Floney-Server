package com.floney.floney.user.dto.request;

import com.floney.floney.user.dto.constant.Provider;
import com.floney.floney.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserSignupRequest {
    private String email;
    private String nickname;
    private String password;
    private boolean marketingAgree;

    public User to() {
        return User.signupBuilder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .marketingAgree(marketingAgree)
                .provider(Provider.EMAIL)
                .build();
    }
}
