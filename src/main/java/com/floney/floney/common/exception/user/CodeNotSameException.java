package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.LogType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CodeNotSameException extends FloneyException {

    public CodeNotSameException(final String code, final String anotherCode) {
        super(ErrorType.INVALID_CODE,
                HttpStatus.BAD_REQUEST,
                LogType.INVALID_CODE, code, anotherCode);
    }
}
