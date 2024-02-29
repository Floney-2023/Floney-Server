package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.FloneyException;
import lombok.Getter;

@Getter
public class NotFoundBookException extends FloneyException {

    private static final String LOG_PATTERN = "가계부 키 [%s] 가계부 %s";
    private String requestKey;

    public NotFoundBookException(String requestKey) {
        super(ErrorType.NOT_FOUND_BOOK, LOG_PATTERN, requestKey);
    }
}
