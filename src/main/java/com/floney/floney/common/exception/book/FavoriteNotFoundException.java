package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class FavoriteNotFoundException extends RuntimeException {

    private final ErrorType errorType;

    public FavoriteNotFoundException(final long id) {
        log.warn("즐겨찾기를 찾을 수 없음 - id: {}", id);
        this.errorType = ErrorType.FAVORITE_NOT_FOUND;
    }
}
