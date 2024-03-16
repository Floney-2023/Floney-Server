package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CodeNotFoundException extends FloneyException {

    public CodeNotFoundException(final String email) {
        super(ErrorType.CODE_NOT_FOUND, HttpStatus.BAD_REQUEST);
        logger.debug("이메일[{}] 인증 시간 만료", email);
    }
}
