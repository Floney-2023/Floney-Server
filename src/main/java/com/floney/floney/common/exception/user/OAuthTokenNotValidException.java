package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.ErrorType;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class OAuthTokenNotValidException extends FloneyException {

    public OAuthTokenNotValidException() {
        super(ErrorType.INVALID_OAUTH_TOKEN, HttpStatus.UNAUTHORIZED);
        logger.warn("외부 로그인 서버에서 잘못된 토큰으로 인한 인증 실패");
    }
}
