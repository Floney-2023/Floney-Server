package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.FloneyException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotFoundBookUserException extends FloneyException {

    public NotFoundBookUserException(String bookKey, String requestUser) {
        super(ErrorType.NOT_FOUND_BOOK_USER, HttpStatus.BAD_REQUEST);
        logger.warn("가계부 키 [{}] 에서 [{}]와 일치하는 {}", bookKey, requestUser, errorType.getMessage());
    }
}
