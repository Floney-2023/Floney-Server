package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class MaxMemberException extends RuntimeException {
    private final ErrorType errorType;

    public MaxMemberException() {
        errorType = ErrorType.MAX_MEMBER;
    }
}
