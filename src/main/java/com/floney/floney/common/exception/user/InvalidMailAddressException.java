package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InvalidMailAddressException extends FloneyException {

    public InvalidMailAddressException(String email) {
        super(ErrorType.INVALID_MAIL_ADDRESS, HttpStatus.BAD_REQUEST);
        logger.warn("유효하지 않은 메일 주소: [{}]", email);
    }
}
