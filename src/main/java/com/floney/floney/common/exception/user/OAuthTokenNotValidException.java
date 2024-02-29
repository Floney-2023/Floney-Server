package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.LogType;
import lombok.Getter;

@Getter
public class OAuthTokenNotValidException extends FloneyException {

    private static final String LOG_PATTERN = "외부 로그인 서버에서 잘못된 토큰으로 인한 인증 실패";

    public OAuthTokenNotValidException() {
        super(ErrorType.INVALID_OAUTH_TOKEN, LogType.INVALID_OAUTH_TOKEN);
    }

}
