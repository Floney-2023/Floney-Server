package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.FloneyException;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class NotFoundCategoryException extends FloneyException {

    public NotFoundCategoryException(String categoryName) {
        super(ErrorType.NOT_FOUND_CATEGORY, HttpStatus.NOT_FOUND);
        logger.warn("[{}] {}", categoryName, errorType.getMessage());
    }
}
