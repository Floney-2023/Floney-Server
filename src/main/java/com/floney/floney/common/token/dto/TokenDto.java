package com.floney.floney.common.token.dto;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenDto {
    @NotNull private final String accessToken;
    @NotNull private final String refreshToken;
}
