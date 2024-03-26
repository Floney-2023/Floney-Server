package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.FloneyException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotFoundRepeatBookLineException extends FloneyException {

    public NotFoundRepeatBookLineException() {
        super(ErrorType.NOT_FOUND_REPEAT_LINE, HttpStatus.BAD_REQUEST);
    }
}
