package com.floney.floney.common.exception.settlement;

import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.LogType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SettlementNotFoundException extends FloneyException {

    public SettlementNotFoundException(final long settlementId) {
        super(ErrorType.NOT_FOUND_SETTLEMENT,
                HttpStatus.BAD_REQUEST,
                LogType.NOT_FOUND_SETTLEMENT, String.valueOf(settlementId));
    }
}
