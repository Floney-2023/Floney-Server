package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.FloneyException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AlreadyExistException extends FloneyException {

    public AlreadyExistException(String target) {
        super(ErrorType.ALREADY_EXIST, HttpStatus.BAD_REQUEST);
        logger.warn("{} data = {}", errorType.getMessage(), target);
    }
}
