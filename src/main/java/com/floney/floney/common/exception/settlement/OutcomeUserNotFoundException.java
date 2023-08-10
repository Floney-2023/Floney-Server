package com.floney.floney.common.exception.settlement;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class OutcomeUserNotFoundException extends RuntimeException {

    private final ErrorType errorType;

    public OutcomeUserNotFoundException() {
        this.errorType = ErrorType.OUTCOME_USER_NOT_FOUND;
    }
}
