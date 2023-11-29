package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class CannotDeleteBookException extends RuntimeException {

    private final ErrorType errorType;

    public CannotDeleteBookException() {
        this.errorType = ErrorType.NO_DELETE_BOOK;
    }
}
