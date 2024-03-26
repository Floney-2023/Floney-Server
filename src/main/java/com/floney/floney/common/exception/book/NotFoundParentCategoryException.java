package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.FloneyException;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class NotFoundParentCategoryException extends FloneyException {

    public NotFoundParentCategoryException(final String categoryName) {
        super(ErrorType.NOT_FOUND_CATEGORY, HttpStatus.BAD_REQUEST);
        logger.warn("[{}] {}", categoryName, errorType.getMessage());
    }
}
