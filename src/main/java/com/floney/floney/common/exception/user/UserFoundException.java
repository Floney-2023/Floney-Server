package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.user.dto.constant.Provider;
import lombok.Getter;

@Getter
public class UserFoundException extends RuntimeException {

    private final ErrorType errorType;
    private final String email;
    private final Provider provider;

    public UserFoundException(String email, Provider provider) {
        this.errorType = ErrorType.USER_FOUND;
        this.email = email;
        this.provider = provider;
    }
}
