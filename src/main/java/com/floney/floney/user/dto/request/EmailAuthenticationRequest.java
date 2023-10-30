package com.floney.floney.user.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@ToString
public class EmailAuthenticationRequest {

    @NotNull
    private final String email;
    @NotNull
    private final String code;

    @Builder
    private EmailAuthenticationRequest(String email, String code) {
        this.email = email;
        this.code = code;
    }

}
