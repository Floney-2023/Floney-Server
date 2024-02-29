package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class SignoutOtherReasonEmptyException extends FloneyException {

    private static final String LOG_PATTERN = "회원 탈퇴 요청에서 value가 비어있음";

    public SignoutOtherReasonEmptyException() {
        super(ErrorType.EMPTY_SIGNOUT_OTHER_REASON, LOG_PATTERN);
    }
}
