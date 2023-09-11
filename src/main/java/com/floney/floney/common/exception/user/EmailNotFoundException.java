package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class EmailNotFoundException extends RuntimeException {

    private final ErrorType errorType;
    private final String email;

    public EmailNotFoundException(final String email) {
        this.errorType = ErrorType.EMAIL_NOT_FOUND;
        this.email = email;
    }
}
