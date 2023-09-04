package com.floney.floney.common.exception.common;

import lombok.Getter;

@Getter
public class NotSubscribeException extends RuntimeException {
    private ErrorType errorType;

    public NotSubscribeException() {
        errorType = ErrorType.NOT_SUBSCRIBE;
    }
}
