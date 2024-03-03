package com.floney.floney.common.exception.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public abstract class FloneyException extends RuntimeException {

    private final transient Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private final ErrorType errorType;
    private final HttpStatus httpStatus;
    private final LogType logType;

    protected FloneyException(final ErrorType errorType, final HttpStatus httpStatus, final LogType logType, final String... logAttributes) {
        super(errorType.getMessage());
        this.errorType = errorType;
        this.httpStatus = httpStatus;
        this.logType = logType;
        printLog(logType.generateLog(logAttributes));
    }

    protected FloneyException(final String message, final ErrorType errorType, HttpStatus httpStatus, final LogType logType, final String... logAttributes) {
        super(message);
        this.errorType = errorType;
        this.httpStatus = httpStatus;
        this.logType = logType;
        printLog(logType.generateLog(logAttributes));
    }

    public String getCode() {
        return errorType.getCode();
    }

    public ErrorType getErrorType() {
        return this.errorType;
    }

    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    private void printLog(final String logMessage) {
        switch (this.logType.getLevel()) {
            case WARN -> logger.warn(logMessage);
            case ERROR -> logger.error(logMessage);
            case DEBUG -> logger.debug(logMessage);
        }
    }
}
