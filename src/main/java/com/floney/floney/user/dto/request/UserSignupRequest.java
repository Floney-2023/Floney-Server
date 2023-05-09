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
@RequiredArgsConstructor
public class UserSignupRequest {
    private String email;
    private String nickname;
    private String password;
    private int marketingAgree;

    public UserSignupRequest(String email, String nickname, String password, int marketingAgree) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.marketingAgree = marketingAgree;
    }
    
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
