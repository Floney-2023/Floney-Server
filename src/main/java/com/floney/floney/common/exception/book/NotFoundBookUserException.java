package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.LogType;
import lombok.Getter;

@Getter
public class NotFoundBookUserException extends FloneyException {

    public NotFoundBookUserException(String bookKey, String requestUser) {
        super(ErrorType.NOT_FOUND_BOOK_USER, LogType.NOT_FOUND_BOOK_USER, bookKey, requestUser, ErrorType.NOT_FOUND_BOOK_USER.getMessage());
    }
}
