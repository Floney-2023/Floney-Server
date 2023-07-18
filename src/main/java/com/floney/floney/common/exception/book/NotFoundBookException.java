package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class NotFoundBookException extends RuntimeException {
    private final ErrorType errorType;

    public NotFoundBookException() {
        errorType = ErrorType.NOT_FOUND_BOOK;
    }
}
