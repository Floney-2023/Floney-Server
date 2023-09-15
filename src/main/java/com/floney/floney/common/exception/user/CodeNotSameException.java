package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class CodeNotSameException extends RuntimeException {

    private final ErrorType errorType;
    private final String code;
    private final String anotherCode;

    public CodeNotSameException(final String code, final String anotherCode) {
        this.errorType = ErrorType.INVALID_CODE;
        this.code = code;
        this.anotherCode = anotherCode;
    }
}
