package com.floney.floney.common.exception;

import lombok.Getter;

@Getter
public class OAuthResponseException extends RuntimeException {

    private final ErrorType errorType;

    public OAuthResponseException() {
        this.errorType = ErrorType.INVALID_OAUTH_RESPONSE;
    }

}
