package com.floney.floney.common.exception.settlement;

import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.LogType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class OutcomeUserNotFoundException extends FloneyException {

    public OutcomeUserNotFoundException() {
        super(ErrorType.NOT_FOUND_OUTCOME_USER,
                HttpStatus.BAD_REQUEST,
                LogType.NOT_FOUND_OUTCOME_USER);
    }
}
