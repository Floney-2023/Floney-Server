package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.ErrorLogType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CannotDeleteBookException extends FloneyException {

    public CannotDeleteBookException() {
        super(ErrorType.NO_DELETE_BOOK,
                HttpStatus.NOT_FOUND,
                ErrorLogType.NO_DELETE_BOOK);
    }
}
