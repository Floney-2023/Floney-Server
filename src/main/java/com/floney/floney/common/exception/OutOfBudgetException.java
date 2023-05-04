package com.floney.floney.common.exception;

import lombok.Getter;

@Getter
public class OutOfBudgetException extends RuntimeException {
    private ErrorType errorType;

    public OutOfBudgetException() {
        errorType = ErrorType.OUT_OF_BUDGET;
    }
}
