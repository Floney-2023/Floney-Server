package com.floney.floney.common.exception.book;

import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.LogType;
import lombok.Getter;

@Getter
public class CannotAnalyzeException extends FloneyException {

    private static final String LOG_PATTERN = "분석이 불가능한 카테고리: %s";

    public CannotAnalyzeException(final CategoryType categoryType) {
        super(ErrorType.CANNOT_ANALYZE, LogType.CANNOT_ANALYZE, categoryType.toString());
    }
}
