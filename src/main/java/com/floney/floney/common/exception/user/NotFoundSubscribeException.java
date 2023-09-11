package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.user.entity.User;
import lombok.Getter;

@Getter
public class NotFoundSubscribeException extends RuntimeException {

    private final ErrorType errorType;
    private final String userEmail;

    public NotFoundSubscribeException(final String email) {
        this.errorType = ErrorType.NOT_FOUND_SUBSCRIBE;
        this.userEmail = email;
    }
}
