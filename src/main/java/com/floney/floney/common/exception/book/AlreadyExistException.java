package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.LogType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AlreadyExistException extends FloneyException {

    public AlreadyExistException(String target) {
        super(ErrorType.ALREADY_EXIST,
                HttpStatus.BAD_REQUEST,
                LogType.ALREADY_EXIST, ErrorType.ALREADY_EXIST.getMessage(), target);
    }
}
