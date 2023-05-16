package com.floney.floney.common.exception;

import lombok.Getter;

@Getter
public class NoAuthorityException extends RuntimeException {
    private final ErrorType errorType;

    public NoAuthorityException() {
        errorType = ErrorType.NO_AUTHORITY;
    }
}
