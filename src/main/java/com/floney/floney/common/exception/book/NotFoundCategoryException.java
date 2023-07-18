package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;


@Getter
public class NotFoundCategoryException extends RuntimeException {
    private final ErrorType errorType;

    public NotFoundCategoryException() {
        errorType = ErrorType.NOT_FOUND_CATEGORY;
    }
}
