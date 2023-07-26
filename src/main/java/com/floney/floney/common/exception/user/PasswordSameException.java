package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class PasswordSameException extends RuntimeException {

    private final ErrorType errorType;

    public PasswordSameException() {
        this.errorType = ErrorType.SAME_PASSWORD;
    }
}
