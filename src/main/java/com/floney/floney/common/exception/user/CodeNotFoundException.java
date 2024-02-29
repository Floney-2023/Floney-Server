package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class CodeNotFoundException extends FloneyException {

    private static final String LOG_PATTERN = "이메일[{}] 인증 시간 만료";

    public CodeNotFoundException(final String email) {
        super(ErrorType.CODE_NOT_FOUND, LOG_PATTERN, email);
    }
}
