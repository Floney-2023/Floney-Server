package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class NotFoundBookException extends RuntimeException {

    private final ErrorType errorType;
    private String requestKey;

    public NotFoundBookException(String requestKey) {
        this.requestKey = requestKey;
        errorType = ErrorType.NOT_FOUND_BOOK;
    }
}
