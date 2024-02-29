package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class PasswordNotSameException extends FloneyException {

    public PasswordNotSameException() {
        super(ErrorType.NOT_SAME_PASSWORD);
    }
}
