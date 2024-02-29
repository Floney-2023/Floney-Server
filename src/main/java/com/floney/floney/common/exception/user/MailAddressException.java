package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.LogType;
import lombok.Getter;

@Getter
public class MailAddressException extends FloneyException {

    private static final String LOG_PATTERN = "유효하지 않은 메일 주소: [%s]";

    public MailAddressException(String email) {
        super(ErrorType.INVALID_MAIL_ADDRESS, LogType.INVALID_MAIL_ADDRESS, email);
    }
}
