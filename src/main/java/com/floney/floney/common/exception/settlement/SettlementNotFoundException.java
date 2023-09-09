package com.floney.floney.common.exception.settlement;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class SettlementNotFoundException extends RuntimeException {

    private final ErrorType errorType;

    public SettlementNotFoundException() {
        this.errorType = ErrorType.NOT_FOUND_SETTLEMENT;
    }
}
