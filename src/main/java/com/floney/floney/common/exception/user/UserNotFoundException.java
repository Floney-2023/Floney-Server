package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.ErrorLogType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserNotFoundException extends FloneyException {

    public UserNotFoundException(String username) {
        super(ErrorType.USER_NOT_FOUND,
                HttpStatus.UNAUTHORIZED,
                ErrorLogType.USER_NOT_FOUND, username);
    }
}
