package com.floney.floney.common.exception.book;

import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.FloneyException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CannotAnalyzeException extends FloneyException {

    public CannotAnalyzeException(final CategoryType categoryType) {
        super(ErrorType.CANNOT_ANALYZE, HttpStatus.BAD_REQUEST);
        logger.warn("분석이 불가능한 카테고리: {}", categoryType);
    }
}
