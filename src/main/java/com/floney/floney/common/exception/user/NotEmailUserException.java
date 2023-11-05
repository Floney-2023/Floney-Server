package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.user.dto.constant.Provider;
import lombok.Getter;

@Getter
public class NotEmailUserException extends RuntimeException {

    private final ErrorType errorType;
    private final Provider provider;

    public NotEmailUserException(final Provider provider) {
        this.errorType = ErrorType.NOT_EMAIL_USER;
        this.provider = provider;
    }
}
