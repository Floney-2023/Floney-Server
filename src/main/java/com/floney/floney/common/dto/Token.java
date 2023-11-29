package com.floney.floney.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
public class Token {
    @NotNull
    private final String accessToken;
    @NotNull
    private final String refreshToken;
}
