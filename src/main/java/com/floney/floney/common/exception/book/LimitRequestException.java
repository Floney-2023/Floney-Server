package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.FloneyException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class LimitRequestException extends FloneyException {

    public LimitRequestException() {
        super(ErrorType.LIMIT, HttpStatus.NOT_ACCEPTABLE);
        logger.warn(getMessage());
    }
}
