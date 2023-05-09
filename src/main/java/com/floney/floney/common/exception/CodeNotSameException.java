package com.floney.floney.common.exception;

import lombok.Getter;

@Getter
public class CodeNotSameException extends RuntimeException {
    private final ErrorType errorType;

    public CodeNotSameException() {
        this.errorType = ErrorType.INVALID_CODE;
    }
}
