package com.floney.floney.user.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@ToString
public class EmailAuthenticationRequest {

    @NotNull(message = "이메일을 입력해주세요")
    @NotBlank(message = "이메일을 입력해주세요")
    private final String email;

    @NotNull(message = "인증 코드를 입력해주세요")
    private final String code;

    @Builder
    private EmailAuthenticationRequest(String email, String code) {
        this.email = email;
        this.code = code;
    }

}
