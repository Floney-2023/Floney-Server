package com.floney.floney.common.exception.common;

import org.springframework.http.HttpStatus;

public abstract class CustomException extends RuntimeException {

    private final ErrorType errorType;
    private final String log;

    protected CustomException(ErrorType errorType, String logPattern, String... logAttributes) {
        super(errorType.getMessage());
        this.errorType = errorType;
        this.log = LogFactory.createErrorLog(logPattern, logAttributes);
    }

    public String getCode() {
        return errorType.getCode();
    }

    public ErrorType getErrorType() {
        return this.errorType;
    }

    public String getLog() {
        return log;
    }

    public HttpStatus getHttpStatus() {
        return errorType.getHttpStatus();
    }
}
