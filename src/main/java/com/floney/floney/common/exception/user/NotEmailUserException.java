package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.user.dto.constant.Provider;
import lombok.Getter;

@Getter
public class NotEmailUserException extends FloneyException {

    private static final String LOG_PATTERN = "이메일 유저가 아니라 간편 유저(%s)임";

    public NotEmailUserException(final Provider provider) {
        super(ErrorType.NOT_EMAIL_USER, LOG_PATTERN, provider.toString());
    }
}
