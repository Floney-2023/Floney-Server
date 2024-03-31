package com.floney.floney.common.exception.favorite;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class FavoriteNotFoundException extends RuntimeException{

    private final ErrorType errorType;

    public FavoriteNotFoundException() {
        this.errorType = ErrorType.FAVORITE_NOT_FOUND;
    }

}
