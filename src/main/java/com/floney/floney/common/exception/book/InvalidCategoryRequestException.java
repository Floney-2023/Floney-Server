package com.floney.floney.common.exception.book;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

/**
 * Exception thrown when a client attempts to use a Korean name for a default category
 * instead of the required categoryKey.
 *
 * This exception provides clear guidance to clients about which categoryKey should be used
 * instead of the Korean name they provided.
 */
@Getter
public class InvalidCategoryRequestException extends RuntimeException {
    private final ErrorType errorType;
    private final String koreanName;
    private final String categoryKey;

    /**
     * @param koreanName The Korean name that was incorrectly used
     * @param categoryKey The correct categoryKey that should be used instead
     */
    public InvalidCategoryRequestException(String koreanName, String categoryKey) {
        super(String.format(
            "Korean name '%s' is not allowed for default categories. Please use categoryKey: '%s'",
            koreanName, categoryKey
        ));
        this.errorType = ErrorType.INVALID_CATEGORY_REQUEST;
        this.koreanName = koreanName;
        this.categoryKey = categoryKey;
    }
}
