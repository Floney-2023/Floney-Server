package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {

    private final ErrorType errorType;
    private final String username;

    public UserNotFoundException(String username) {
        this.errorType = ErrorType.USER_NOT_FOUND;
        this.username = username;
    }
}
