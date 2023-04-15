package com.floney.floney.common.exception;


import lombok.Getter;

@Getter
public class ErrorResponse {

    private String message;
    private String code;

    public ErrorResponse(ErrorType errorType) {
        this.message = errorType.getMessage();
        this.code = errorType.getCode();
    }

    public static ErrorResponse of(ErrorType errorType) {
        return new ErrorResponse(errorType);
    }
}
