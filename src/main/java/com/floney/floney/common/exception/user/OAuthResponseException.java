package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class OAuthResponseException extends RuntimeException {

    private final ErrorType errorType;

    public OAuthResponseException() {
        this.errorType = ErrorType.INVALID_OAUTH_RESPONSE;
    }

}
