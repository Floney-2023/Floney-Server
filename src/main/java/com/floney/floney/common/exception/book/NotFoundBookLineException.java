package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.ErrorLogType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotFoundBookLineException extends FloneyException {

    public NotFoundBookLineException() {
        super(ErrorType.NOT_FOUND_BOOK_LINE,
                HttpStatus.BAD_REQUEST,
                ErrorLogType.NOT_FOUND_BOOK_LINE);
    }

}
