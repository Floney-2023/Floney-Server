package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class MailAddressException extends RuntimeException {
    private final ErrorType errorType;

    public MailAddressException() {
        this.errorType = ErrorType.INVALID_MAIL_ADDRESS;
    }
}
