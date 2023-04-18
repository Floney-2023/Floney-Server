package com.floney.floney.common.exception;

import lombok.Getter;

@Getter
public class MaxMemberException extends RuntimeException {
    private final ErrorType errorType;

    public MaxMemberException() {
        errorType = ErrorType.MAX_MEMBER;
    }
}
