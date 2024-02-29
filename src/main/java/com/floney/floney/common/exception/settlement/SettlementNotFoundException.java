package com.floney.floney.common.exception.settlement;

import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.FloneyException;
import lombok.Getter;

@Getter
public class SettlementNotFoundException extends FloneyException {

    private static final String LOG_PATTERN = "settlement id [%s]의 정산 내역이 존재하지 않음";

    public SettlementNotFoundException(final long settlementId) {
        super(ErrorType.NOT_FOUND_SETTLEMENT, LOG_PATTERN, String.valueOf(settlementId));
    }
}
