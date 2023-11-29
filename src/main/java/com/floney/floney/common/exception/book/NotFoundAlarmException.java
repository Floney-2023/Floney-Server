package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class NotFoundAlarmException extends RuntimeException {

    private final ErrorType errorType;
    private Long requestKey;

    public NotFoundAlarmException(Long requestKey) {
        this.requestKey = requestKey;
        errorType = ErrorType.NOT_FOUND_ALARM;
    }
}
