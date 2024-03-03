package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.LogType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PasswordNotSameException extends FloneyException {

    public PasswordNotSameException() {
        super(ErrorType.NOT_SAME_PASSWORD,
                HttpStatus.BAD_REQUEST,
                LogType.NOT_SAME_PASSWORD);
    }
}
