package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class SignoutOtherReasonEmptyException extends RuntimeException {

    private final ErrorType errorType;

    public SignoutOtherReasonEmptyException() {
        this.errorType = ErrorType.EMPTY_SIGNOUT_OTHER_REASON;
    }
}
