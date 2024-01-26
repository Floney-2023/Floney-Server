package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class PasswordNotSameException extends RuntimeException {

    private final ErrorType errorType;

    public PasswordNotSameException() {
        this.errorType = ErrorType.NOT_SAME_PASSWORD;
    }
}
