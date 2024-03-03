package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.LogType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MailAddressException extends FloneyException {

    public MailAddressException(String email) {
        super(ErrorType.INVALID_MAIL_ADDRESS,
                HttpStatus.BAD_REQUEST,
                LogType.INVALID_MAIL_ADDRESS, email);
    }
}
