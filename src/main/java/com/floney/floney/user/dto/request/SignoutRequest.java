package com.floney.floney.user.dto.request;

import com.floney.floney.user.dto.constant.SignoutType;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class SignoutRequest {

    @NotNull
    private SignoutType type;
    private String value;
}
