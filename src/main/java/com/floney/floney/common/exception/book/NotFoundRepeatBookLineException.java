package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class NotFoundRepeatBookLineException extends RuntimeException {
    private final ErrorType errorType;

    public NotFoundRepeatBookLineException() {
        errorType = ErrorType.NOT_FOUND_REPEAT_LINE;
    }
}
