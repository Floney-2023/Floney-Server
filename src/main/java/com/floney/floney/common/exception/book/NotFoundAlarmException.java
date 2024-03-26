package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.FloneyException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotFoundAlarmException extends FloneyException {

    public NotFoundAlarmException(Long requestKey) {
        super(ErrorType.NOT_FOUND_ALARM, HttpStatus.BAD_REQUEST);
        logger.error("{} id = {} \n [ERROR_MSG] : {} \n [ERROR_STACK] : {}", errorType.getMessage(), requestKey, getMessage(), getStackTrace());
    }
}
