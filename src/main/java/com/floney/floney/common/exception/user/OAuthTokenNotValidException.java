package com.floney.floney.common.exception.user;

import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.LogType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class OAuthTokenNotValidException extends FloneyException {

    public OAuthTokenNotValidException() {
        super(ErrorType.INVALID_OAUTH_TOKEN,
                HttpStatus.UNAUTHORIZED,
                LogType.INVALID_OAUTH_TOKEN);
    }

}
