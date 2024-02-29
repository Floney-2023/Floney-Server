package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.LogType;
import com.floney.floney.user.dto.constant.Provider;
import lombok.Getter;

@Getter
public class NotEmailUserException extends FloneyException {

    public NotEmailUserException(final Provider provider) {
        super(ErrorType.NOT_EMAIL_USER, LogType.NOT_EMAIL_USER, provider.toString());
    }
}
