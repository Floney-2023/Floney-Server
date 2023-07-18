package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class EmailNotValidException extends RuntimeException {

    private final ErrorType errorType;

    public EmailNotValidException() {
        this.errorType = ErrorType.INVALID_EMAIL;
    }
}
