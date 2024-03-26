package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CodeNotSameException extends FloneyException {

    public CodeNotSameException(final String code, final String anotherCode) {
        super(ErrorType.INVALID_CODE, HttpStatus.BAD_REQUEST);
        logger.debug("일치하지 않는 이메일 인증 코드: [{}], [{}]", code, anotherCode);
    }
}
