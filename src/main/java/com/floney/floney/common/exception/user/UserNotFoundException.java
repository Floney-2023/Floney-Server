package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.ErrorType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserNotFoundException extends FloneyException {

    public UserNotFoundException(String username) {
        super(ErrorType.USER_NOT_FOUND, HttpStatus.UNAUTHORIZED);
        logger.warn("저장되지 않은 유저 정보: [{}]", username);
    }
}
