package com.floney.floney.common.exception;

import lombok.Getter;

@Getter
public class ProviderNotFoundException extends RuntimeException {

    private final ErrorType errorType;

    public ProviderNotFoundException() {
        this.errorType = ErrorType.INVALID_PROVIDER;
    }
}
