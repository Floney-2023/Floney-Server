package com.floney.floney.common.exception.book;

import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class CannotAnalyzeException extends RuntimeException {

    private final ErrorType errorType;
    private final CategoryType categoryType;

    public CannotAnalyzeException(final CategoryType categoryType) {
        this.errorType = ErrorType.CANNOT_ANALYZE;
        this.categoryType = categoryType;
    }
}
