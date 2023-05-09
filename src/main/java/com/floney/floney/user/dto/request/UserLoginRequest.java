package com.floney.floney.user.dto.request;

import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserLoginRequest {
    private String email;
    
    private String password;
    public UserLoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

}
