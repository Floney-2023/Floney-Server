package com.floney.floney.common.exception;

import lombok.Getter;

@Getter
public class NotFoundBookException extends RuntimeException {
    private final ErrorType errorType;

    public NotFoundBookException() {
        errorType = ErrorType.NOT_FOUND_BOOK;
    }
}
