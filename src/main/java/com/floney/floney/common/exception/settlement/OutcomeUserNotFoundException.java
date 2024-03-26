package com.floney.floney.common.exception.settlement;

import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.FloneyException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class OutcomeUserNotFoundException extends FloneyException {

    public OutcomeUserNotFoundException() {
        super(ErrorType.NOT_FOUND_OUTCOME_USER, HttpStatus.BAD_REQUEST);
        logger.warn("Request Body에서 지출 내역의 유저가 유저 목록에 존재하지 않음");
    }
}
