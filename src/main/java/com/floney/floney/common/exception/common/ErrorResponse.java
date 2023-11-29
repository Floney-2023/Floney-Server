package com.floney.floney.common.exception.common;


import lombok.Getter;

@Getter
public class ErrorResponse {

    private final String message;
    private final String code;

    private ErrorResponse(ErrorType errorType) {
        this.message = errorType.getMessage();
        this.code = errorType.getCode();
    }

    private ErrorResponse(final String message, final String code) {
        this.message = message;
        this.code = code;
    }

    public static ErrorResponse of(ErrorType errorType) {
        return new ErrorResponse(errorType);
    }

    public static ErrorResponse of(ErrorType errorType, String customMessage) {
        return new ErrorResponse(customMessage, errorType.getCode());
    }
}
