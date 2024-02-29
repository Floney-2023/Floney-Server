package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.LogType;
import lombok.Getter;

@Getter
public class SignoutOtherReasonEmptyException extends FloneyException {

    public SignoutOtherReasonEmptyException() {
        super(ErrorType.EMPTY_SIGNOUT_OTHER_REASON, LogType.EMPTY_SIGNOUT_OTHER_REASON);
    }
}
