package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class CodeNotFoundException extends RuntimeException{

    private final ErrorType errorType;
    private final String email;

    public CodeNotFoundException(final String email) {
        this.errorType = ErrorType.CODE_NOT_FOUND;
        this.email = email;
    }
}
