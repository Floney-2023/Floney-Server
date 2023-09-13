package com.floney.floney.user.dto.request;

import javax.validation.constraints.NotNull;

import lombok.*;

@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
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
