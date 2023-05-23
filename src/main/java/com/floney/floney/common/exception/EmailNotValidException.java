package com.floney.floney.common.exception;

import lombok.Getter;

@Getter
public class EmailNotValidException extends RuntimeException {

    private final ErrorType errorType;

    public EmailNotValidException() {
        this.errorType = ErrorType.INVALID_EMAIL;
    }
}
