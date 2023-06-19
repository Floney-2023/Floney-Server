package com.floney.floney.common.dto;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Token {
    @NotNull private final String accessToken;
    @NotNull private final String refreshToken;
}
