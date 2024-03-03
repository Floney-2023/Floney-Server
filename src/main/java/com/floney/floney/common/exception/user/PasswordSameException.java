package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.ErrorLogType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PasswordSameException extends FloneyException {

    public PasswordSameException() {
        super(ErrorType.SAME_PASSWORD,
                HttpStatus.BAD_REQUEST,
                ErrorLogType.SAME_PASSWORD);
    }
}
