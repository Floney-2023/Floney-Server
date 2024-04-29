package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class FavoriteSizeInvalidException extends RuntimeException {

    private final ErrorType errorType;

    public FavoriteSizeInvalidException(final String bookKey) {
        log.warn("즐겨찾기 개수 초과 - 가계부: {}", bookKey);
        this.errorType = ErrorType.INVALID_FAVORITE_SIZE;
    }
}
