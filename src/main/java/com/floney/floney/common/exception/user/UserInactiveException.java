package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class UserInactiveException extends RuntimeException {
    private final ErrorType errorType;

    public UserInactiveException() {
        this.errorType = ErrorType.USER_INACTIVE;
    }
}
