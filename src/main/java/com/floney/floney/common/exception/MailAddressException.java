package com.floney.floney.common.exception;

import lombok.Getter;

@Getter
public class MailAddressException extends RuntimeException {
    private final ErrorType errorType;

    public MailAddressException() {
        this.errorType = ErrorType.INVALID_MAIL_ADDRESS;
    }
}
