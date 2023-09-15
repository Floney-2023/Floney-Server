package com.floney.floney.user.dto.request;

import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginRequest {

    @NotNull private String email;
    @NotNull private String password;

    @Override
    public String toString() {
        return "LoginRequest{" +
            "email='" + email + '\'' +
            '}';
    }
}
