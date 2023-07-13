package com.floney.floney.common.exception;

import lombok.Getter;

@Getter
public class SettlementNotFoundException extends RuntimeException {

    private final ErrorType errorType;

    public SettlementNotFoundException() {
        this.errorType = ErrorType.SETTLEMENT_NOT_FOUND;
    }
}
