package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;

@Getter
public class PasswordSameException extends FloneyException {

    public PasswordSameException() {
        super(ErrorType.SAME_PASSWORD);
    }
}
