package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.LogType;
import com.floney.floney.user.dto.constant.Provider;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotEmailUserException extends FloneyException {

    public NotEmailUserException(final Provider provider) {
        super(ErrorType.NOT_EMAIL_USER,
                HttpStatus.BAD_REQUEST,
                LogType.NOT_EMAIL_USER, provider.toString());
    }
}
