package com.floney.floney.common.exception;

import lombok.Getter;

@Getter
public class UserSignoutException extends RuntimeException {
    private final ErrorType errorType;

    public UserSignoutException() {
        this.errorType = ErrorType.USER_SIGNOUT;
    }
}
