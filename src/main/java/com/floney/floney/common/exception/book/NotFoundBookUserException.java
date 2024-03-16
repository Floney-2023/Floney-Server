package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.ErrorLogType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotFoundBookUserException extends FloneyException {

    public NotFoundBookUserException(String bookKey, String requestUser) {
        super(ErrorType.NOT_FOUND_BOOK_USER, HttpStatus.NOT_FOUND, ErrorLogType.NOT_FOUND_BOOK_USER);
        printLog(bookKey, requestUser, ErrorType.NOT_FOUND_BOOK_USER.getMessage());
    }
}
