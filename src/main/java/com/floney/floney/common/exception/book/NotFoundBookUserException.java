package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class NotFoundBookUserException extends RuntimeException {
    private final ErrorType errorType;

    public NotFoundBookUserException() {
        errorType = ErrorType.NOT_FOUND_BOOK_USER;
    }
}
