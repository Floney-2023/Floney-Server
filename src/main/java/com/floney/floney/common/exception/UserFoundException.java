package com.floney.floney.common.exception;

import lombok.Getter;

@Getter
public class UserFoundException extends RuntimeException {
    private final ErrorType errorType;
    private final String provider;

    public UserFoundException(String provider) {
        this.errorType = ErrorType.USER_FOUND;
        this.provider = provider;
    }
}
