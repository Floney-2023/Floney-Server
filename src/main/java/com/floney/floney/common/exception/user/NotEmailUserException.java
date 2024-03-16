package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.user.dto.constant.Provider;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotEmailUserException extends FloneyException {

    public NotEmailUserException(final Provider provider) {
        super(ErrorType.NOT_EMAIL_USER, HttpStatus.BAD_REQUEST);
        logger.warn("이메일 유저가 아니라 간편 유저({})임", provider);
    }
}
