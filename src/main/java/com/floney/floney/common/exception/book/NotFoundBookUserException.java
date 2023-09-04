package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class NotFoundBookUserException extends RuntimeException {
    private final ErrorType errorType;
    private final String bookKey;
    private final String requestUser;

    public NotFoundBookUserException(String bookKey, String requestUser) {
        errorType = ErrorType.NOT_FOUND_BOOK_USER;
        this.bookKey = bookKey;
        this.requestUser = requestUser;
    }
}
