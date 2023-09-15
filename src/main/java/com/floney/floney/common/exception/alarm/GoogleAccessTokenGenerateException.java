package com.floney.floney.common.exception.alarm;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class GoogleAccessTokenGenerateException extends RuntimeException {

    private final ErrorType errorType;

    public GoogleAccessTokenGenerateException() {
        this.errorType = ErrorType.FAIL_TO_GENERATE_TOKEN;
    }
}
