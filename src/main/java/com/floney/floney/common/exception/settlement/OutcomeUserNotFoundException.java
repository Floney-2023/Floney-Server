package com.floney.floney.common.exception.settlement;

import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.LogType;
import lombok.Getter;

@Getter
public class OutcomeUserNotFoundException extends FloneyException {

    public OutcomeUserNotFoundException() {
        super(ErrorType.NOT_FOUND_OUTCOME_USER, LogType.NOT_FOUND_OUTCOME_USER);
    }
}
