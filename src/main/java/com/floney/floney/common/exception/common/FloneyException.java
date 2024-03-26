package com.floney.floney.common.exception.common;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

@Getter
public abstract class FloneyException extends RuntimeException {

    protected final transient Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    protected final ErrorType errorType;
    protected final HttpStatus httpStatus;

    protected FloneyException(final ErrorType errorType, final HttpStatus httpStatus) {
        super(errorType.getMessage());
        this.errorType = errorType;
        this.httpStatus = httpStatus;
    }

    protected FloneyException(final String message, final ErrorType errorType, HttpStatus httpStatus) {
        super(message);
        this.errorType = errorType;
        this.httpStatus = httpStatus;
    }

}
