package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class MailAddressException extends RuntimeException {

    private final ErrorType errorType;
    private final String email;

    public MailAddressException(String email) {
        this.errorType = ErrorType.INVALID_MAIL_ADDRESS;
        this.email = email;
    }
}
