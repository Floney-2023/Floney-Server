package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class NotFoundBookLineException extends RuntimeException {
    private final ErrorType errorType;

    public NotFoundBookLineException() {
        errorType = ErrorType.NOT_FOUND_BOOK_LINE;
    }
}
