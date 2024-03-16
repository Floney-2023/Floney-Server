package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.ErrorLogType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CodeNotFoundException extends FloneyException {

    public CodeNotFoundException(final String email) {
        super(ErrorType.CODE_NOT_FOUND, HttpStatus.BAD_REQUEST, ErrorLogType.CODE_NOT_FOUND);
        printLog(email);
    }
}
