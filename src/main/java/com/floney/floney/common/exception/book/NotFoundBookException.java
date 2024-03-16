package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.ErrorLogType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotFoundBookException extends FloneyException {

    public NotFoundBookException(String requestKey) {
        super(ErrorType.NOT_FOUND_BOOK, HttpStatus.NOT_FOUND, ErrorLogType.NOT_FOUND_BOOK);
        printLog(requestKey, ErrorType.NOT_FOUND_BOOK.getMessage());
    }
}
