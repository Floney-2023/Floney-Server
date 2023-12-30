package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class AlreadyExistException extends RuntimeException {
    private final ErrorType errorType;
    private final String target;

    public AlreadyExistException(String target) {
        this.errorType = ErrorType.ALREADY_EXIST;
        this.target = target;
    }
}
