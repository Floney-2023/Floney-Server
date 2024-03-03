package com.floney.floney.common.exception.book;

import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.ErrorLogType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CannotAnalyzeException extends FloneyException {

    public CannotAnalyzeException(final CategoryType categoryType) {
        super(ErrorType.CANNOT_ANALYZE,
                HttpStatus.BAD_REQUEST,
                ErrorLogType.CANNOT_ANALYZE, categoryType.toString());
    }
}
