package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class ExcelMakingException extends RuntimeException {

    private final ErrorType errorType;

    public ExcelMakingException(final String message) {
        super(message);
        this.errorType = ErrorType.FAIL_TO_CREATE_EXCEL;
    }
}
