package com.floney.floney.user.dto.request;

import com.floney.floney.user.dto.constant.Provider;
import com.floney.floney.user.entity.User;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserSignupRequest {
    @NotNull private String email;
    @NotNull private String nickname;
    @NotNull private String password;
    @NotNull private boolean marketingAgree;

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
