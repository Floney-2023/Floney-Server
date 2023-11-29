package com.floney.floney.user.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginRequest {

    @NotNull(message = "이메일을 입력해주세요")
    @NotBlank(message = "이메일을 입력해주세요")
    private String email;

    @NotNull(message = "비밀번호를 입력해주세요")
    @NotBlank(message = "비밀번호를 입력해주세요")
    private String password;

    @Override
    public String toString() {
        return "LoginRequest{" +
                "email='" + email + '\'' +
                '}';
    }
}
