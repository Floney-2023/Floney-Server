package com.floney.floney.common.exception;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {
    private final ErrorType errorType;

    public UserNotFoundException() {
        this.errorType = ErrorType.USER_NOT_FOUND;
    }
}
