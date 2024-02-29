package com.floney.floney.common.exception.common;

import org.springframework.http.HttpStatus;

public abstract class FloneyException extends RuntimeException {

    private final ErrorType errorType;
    private final LogType logType;
    private final String logMessage;

    protected FloneyException(final ErrorType errorType, final LogType logType, final String... logAttributes) {
        super(errorType.getMessage());
        this.errorType = errorType;
        this.logType = logType;
        this.logMessage = logType.generateLog(logAttributes);
    }

    protected FloneyException(final String message, final ErrorType errorType, final LogType logType, final String... logAttributes) {
        super(message);
        this.errorType = errorType;
        this.logType = logType;
        this.logMessage = logType.generateLog(logAttributes);
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
        return this.logType.getLevel();
    }

    public String getLogMessage() {
        return this.logMessage;
    }
}
