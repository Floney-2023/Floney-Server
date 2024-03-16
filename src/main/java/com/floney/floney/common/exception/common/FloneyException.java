package com.floney.floney.common.exception.common;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

@Getter
public abstract class FloneyException extends RuntimeException {

    private final transient Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private final ErrorType errorType;
    private final HttpStatus httpStatus;
    private final ErrorLogType errorLogType;

    protected FloneyException(final ErrorType errorType, final HttpStatus httpStatus, final ErrorLogType errorLogType) {
        super(errorType.getMessage());
        this.errorType = errorType;
        this.httpStatus = httpStatus;
        this.errorLogType = errorLogType;
    }

    protected FloneyException(final String message, final ErrorType errorType, HttpStatus httpStatus, final ErrorLogType errorLogType) {
        super(message);
        this.errorType = errorType;
        this.httpStatus = httpStatus;
        this.errorLogType = errorLogType;
    }

    protected void printLog(final String... logAttributes) {
        String logMessage = this.errorLogType.generateLogMessage(logAttributes);
        switch (this.errorLogType.getLevel()) {
            case WARN -> logger.warn(logMessage);
            case ERROR -> logger.error(logMessage);
            case DEBUG -> logger.debug(logMessage);
        }
    }

}
