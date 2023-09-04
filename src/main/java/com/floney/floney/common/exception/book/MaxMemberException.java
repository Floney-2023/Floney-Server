package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class MaxMemberException extends RuntimeException {
    private final ErrorType errorType;
    private final String bookKey;
    private final int memberCount;

    public MaxMemberException(final String bookKey, final int memberCount) {
        errorType = ErrorType.MAX_MEMBER;
        this.bookKey = bookKey;
        this.memberCount = memberCount;

    }
}
