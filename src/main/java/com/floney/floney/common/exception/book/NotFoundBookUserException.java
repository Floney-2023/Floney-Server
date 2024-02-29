package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.FloneyException;
import lombok.Getter;

@Getter
public class NotFoundBookUserException extends FloneyException {

    private static final String LOG_PATTERN = "가계부 키 [%s] 에서 [%s]와 일치하는 %s";

    public NotFoundBookUserException(String bookKey, String requestUser) {
        super(ErrorType.NOT_FOUND_BOOK_USER, LOG_PATTERN, bookKey, requestUser, ErrorType.NOT_FOUND_BOOK_USER.getMessage());
    }
}
