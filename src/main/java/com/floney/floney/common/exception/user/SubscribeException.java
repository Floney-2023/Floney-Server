package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class SubscribeException extends RuntimeException {

    private final ErrorType errorType;

    public SubscribeException() {
        this.errorType = ErrorType.SUBSCRIBE;
    }
}
