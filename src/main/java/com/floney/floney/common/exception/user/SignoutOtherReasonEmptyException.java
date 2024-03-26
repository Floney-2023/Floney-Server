package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SignoutOtherReasonEmptyException extends FloneyException {

    public SignoutOtherReasonEmptyException() {
        super(ErrorType.EMPTY_SIGNOUT_OTHER_REASON, HttpStatus.BAD_REQUEST);
        logger.warn("회원 탈퇴 요청에서 value가 비어있음");
    }
}
