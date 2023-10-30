package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class LimitRequestException extends RuntimeException {
    private final ErrorType errorType;

    public LimitRequestException() {
        this.errorType = ErrorType.LIMIT;
    }
}
