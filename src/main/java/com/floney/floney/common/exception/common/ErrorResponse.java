package com.floney.floney.common.exception.common;


import lombok.Getter;

@Getter
public class ErrorResponse {

    private final String message;
    private final String code;
    public ErrorResponse(ErrorType errorType) {
        this.message = errorType.getMessage();
        this.code = errorType.getCode();
    }

    public static ErrorResponse of(ErrorType errorType) {
        return new ErrorResponse(errorType);
    }
}
