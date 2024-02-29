package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.LogType;
import com.floney.floney.user.dto.constant.Provider;
import lombok.Getter;

@Getter
public class UserFoundException extends FloneyException {

    private static final String LOG_PATTERN = "이미 존재하는 유저: [%s]";
    private final String provider;

    public UserFoundException(String email, Provider provider) {
        super(ErrorType.USER_FOUND, LogType.USER_FOUND, email);
        this.provider = String.valueOf(provider);
    }

    public String getProvider() {
        return provider;
    }
}
