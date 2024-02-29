package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class CodeNotSameException extends FloneyException {

    private static final String LOG_PATTERN = "일치하지 않는 이메일 인증 코드: [%s], [%s]";

    public CodeNotSameException(final String code, final String anotherCode) {
        super(ErrorType.INVALID_CODE, LOG_PATTERN, code, anotherCode);
    }
}
