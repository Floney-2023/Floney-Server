package com.floney.floney.user.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class EmailAuthenticationRequest {
    private final String email;
    private final String code;

    @Builder
    private EmailAuthenticationRequest(String email, String code) {
        this.email = email;
        this.code = code;
    }
}
