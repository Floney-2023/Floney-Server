package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.LogType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EmailNotFoundException extends FloneyException {

    public EmailNotFoundException(final String email) {
        super(ErrorType.EMAIL_NOT_FOUND,
                HttpStatus.BAD_REQUEST,
                LogType.EMAIL_NOT_FOUND, email);
    }
}
