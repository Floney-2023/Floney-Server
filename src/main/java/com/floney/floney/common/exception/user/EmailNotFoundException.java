package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.LogType;
import lombok.Getter;

@Getter
public class EmailNotFoundException extends FloneyException {

    private static final String LOG_PATTERN = "이메일(%s) 찾기 실패";

    public EmailNotFoundException(final String email) {
        super(ErrorType.EMAIL_NOT_FOUND, LogType.EMAIL_NOT_FOUND, email);
    }
}
