package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class ProviderNotFoundException extends RuntimeException {

    private final ErrorType errorType;

    public ProviderNotFoundException() {
        this.errorType = ErrorType.INVALID_PROVIDER;
    }
}
