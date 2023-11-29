package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class OAuthTokenNotValidException extends RuntimeException {

    private final ErrorType errorType;

    public OAuthTokenNotValidException() {
        this.errorType = ErrorType.INVALID_OAUTH_TOKEN;
    }

}
