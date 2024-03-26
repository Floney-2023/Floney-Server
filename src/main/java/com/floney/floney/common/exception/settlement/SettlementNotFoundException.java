package com.floney.floney.common.exception.settlement;

import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.FloneyException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SettlementNotFoundException extends FloneyException {

    public SettlementNotFoundException(final long settlementId) {
        super(ErrorType.NOT_FOUND_SETTLEMENT, HttpStatus.BAD_REQUEST);
        logger.warn("settlement id [{}]의 정산 내역이 존재하지 않음", settlementId);
    }
}
