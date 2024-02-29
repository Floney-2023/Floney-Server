package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.FloneyException;
import lombok.Getter;

@Getter
public class NotFoundBookLineException extends FloneyException {

    public NotFoundBookLineException() {
        super(ErrorType.NOT_FOUND_BOOK_LINE);
    }

}
