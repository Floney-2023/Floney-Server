package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;


@Getter
public class NotFoundCategoryException extends RuntimeException {
    private final ErrorType errorType;
    private final String categoryName;

    public NotFoundCategoryException(String categoryName) {
        errorType = ErrorType.NOT_FOUND_CATEGORY;
        this.categoryName = categoryName;
    }
}
