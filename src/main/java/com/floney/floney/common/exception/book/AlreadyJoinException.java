package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class AlreadyJoinException extends RuntimeException {
    private final ErrorType errorType;
    private final String userEmail;

    public AlreadyJoinException(String userEmail) {
        this.errorType = ErrorType.ALREADY_JOIN;
        this.userEmail = userEmail;
    }
}
