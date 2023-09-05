package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class CannotDeleteBookException extends RuntimeException {
    private final ErrorType errorType;
    private final long leftMemberCount;

    public CannotDeleteBookException(long leftMemberCount) {
        this.errorType = ErrorType.NO_DELETE_BOOK;
        this.leftMemberCount = leftMemberCount;
    }
}
