package com.floney.floney.common.exception.common;

import org.springframework.http.HttpStatus;

public abstract class FloneyException extends RuntimeException {

    private final ErrorType errorType;
    private CustomLog log;

    protected FloneyException(ErrorType errorType, String logPattern, String... logAttributes) {
        super(errorType.getMessage());
        String logMessage = String.format(logPattern, (Object[]) logAttributes);
        this.errorType = errorType;
        this.log = CustomLog.of(errorType.getLogLevel(), logMessage);

    }

    protected FloneyException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }

    protected FloneyException(String message, ErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }

    public String getCode() {
        return errorType.getCode();
    }

    public ErrorType getErrorType() {
        return this.errorType;
    }

    public HttpStatus getHttpStatus() {
        return errorType.getHttpStatus();
    }

    public CustomLogLevel getLogLevel() {
        return this.errorType.getLogLevel();
    }

    public String getLogMessage() {
        return this.log.getMessage();
    }
}
