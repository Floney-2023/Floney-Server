package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class NotFoundBookUserException extends RuntimeException {

    private final ErrorType errorType;

    public NotFoundBookUserException(final String bookKey, final String userEmail) {
        log.warn("사용자가 자신이 참여하지 않은 가계부로 요청 - 가계부: {}, 사용자: {}", bookKey, userEmail);
        this.errorType = ErrorType.NOT_FOUND_BOOK_USER;
    }
}
