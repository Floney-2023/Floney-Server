package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.FloneyException;
import lombok.Getter;

@Getter
public class AlreadyExistException extends FloneyException {

    private static final String LOG_PATTERN = "%s data = %s";

    public AlreadyExistException(String target) {
        super(ErrorType.ALREADY_EXIST, LOG_PATTERN, ErrorType.ALREADY_EXIST.getMessage(), target);
    }
}
