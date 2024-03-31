package com.floney.floney.common.exception.favorite;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class FavoriteAlreadyRegisteredException extends RuntimeException {

    private final ErrorType errorType;

    public FavoriteAlreadyRegisteredException() {
        this.errorType = ErrorType.FAVORITE_ALREADY_REGISTERED;
    }

}
