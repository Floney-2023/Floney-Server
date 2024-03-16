package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.FloneyException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EmptyOAuthResponseException extends FloneyException {

    public EmptyOAuthResponseException() {
        super(ErrorType.INVALID_OAUTH_RESPONSE, HttpStatus.INTERNAL_SERVER_ERROR);
        logger.error("외부 로그인 서버에서 빈 응답을 받음 \n [ERROR_MSG] : {} \n [ERROR STACK] : {}", getMessage(), getStackTrace());
    }
}
