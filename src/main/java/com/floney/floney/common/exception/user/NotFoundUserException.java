package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class NotFoundUserException extends RuntimeException {

    private final ErrorType errorType;
    private final String username;

    public NotFoundUserException(String username) {
        this.errorType = ErrorType.USER_NOT_FOUND;
        this.username = username;
    }

    public NotFoundUserException() {
        this.errorType = ErrorType.USER_NOT_FOUND;
        this.username = null;
    }
}
