package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.LogType;
import lombok.Getter;

@Getter
public class NotFoundBookException extends FloneyException {

    public NotFoundBookException(String requestKey) {
        super(ErrorType.NOT_FOUND_BOOK, LogType.NOT_FOUND_BOOK, requestKey, ErrorType.NOT_FOUND_BOOK.getMessage());
    }
}
