package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class UserSignoutException extends RuntimeException {
    private final ErrorType errorType;

    public UserSignoutException() {
        this.errorType = ErrorType.USER_SIGNOUT;
    }
}
