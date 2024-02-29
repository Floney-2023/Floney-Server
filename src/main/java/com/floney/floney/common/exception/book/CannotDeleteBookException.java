package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.LogType;
import lombok.Getter;

@Getter
public class CannotDeleteBookException extends FloneyException {

    public CannotDeleteBookException() {
        super(ErrorType.NO_DELETE_BOOK, LogType.NO_DELETE_BOOK);
    }
}
