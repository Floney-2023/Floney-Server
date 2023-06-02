package com.floney.floney.common.exception;

import lombok.Getter;

@Getter
public class OAuthTokenNotValidException extends RuntimeException {

    private final ErrorType errorType;

    public OAuthTokenNotValidException() {
        this.errorType = ErrorType.INVALID_OAUTH_TOKEN;
    }

}
