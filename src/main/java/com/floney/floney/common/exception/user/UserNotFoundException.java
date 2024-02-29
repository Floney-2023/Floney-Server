package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.LogType;
import lombok.Getter;

@Getter
public class UserNotFoundException extends FloneyException {

    public UserNotFoundException(String username) {
        super(ErrorType.USER_NOT_FOUND, LogType.USER_NOT_FOUND, username);
    }
}
