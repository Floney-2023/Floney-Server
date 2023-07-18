package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class OutOfBudgetException extends RuntimeException {
    private ErrorType errorType;

    public OutOfBudgetException() {
        errorType = ErrorType.OUT_OF_BUDGET;
    }
}
