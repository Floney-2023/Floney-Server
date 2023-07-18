package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {
    private final ErrorType errorType;

    public UserNotFoundException() {
        this.errorType = ErrorType.USER_NOT_FOUND;
    }
}
