package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.ErrorLogType;
import com.floney.floney.user.dto.constant.Provider;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserFoundException extends FloneyException {

    private final String provider;

    public UserFoundException(String email, Provider provider) {
        super(ErrorType.USER_FOUND,
                HttpStatus.BAD_REQUEST,
                ErrorLogType.USER_FOUND, email);
        this.provider = String.valueOf(provider);
    }

    public String getProvider() {
        return provider;
    }
}
