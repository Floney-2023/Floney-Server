package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;


@Getter
public class NotFoundParentCategoryException extends RuntimeException {

    private final ErrorType errorType;
    private final String categoryName;

    public NotFoundParentCategoryException(final String categoryName) {
        errorType = ErrorType.NOT_FOUND_CATEGORY;
        this.categoryName = categoryName;
    }
}
