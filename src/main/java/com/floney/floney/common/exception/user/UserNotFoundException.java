package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.LogType;
import lombok.Getter;

@Getter
public class UserNotFoundException extends FloneyException {

    private static final String LOG_PATTERN = "저장되지 않은 유저 정보: [%s]";

    public UserNotFoundException(String username) {
        super(ErrorType.USER_NOT_FOUND, LogType.USER_NOT_FOUND, username);
    }
}
