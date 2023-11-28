package com.floney.floney.common.exception.settlement;

import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class SettlementNotFoundException extends RuntimeException {

    private final ErrorType errorType;
    private final long settlementId;

    public SettlementNotFoundException(final long settlementId) {
        this.errorType = ErrorType.NOT_FOUND_SETTLEMENT;
        this.settlementId = settlementId;
    }
}
