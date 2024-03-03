package com.floney.floney.common.exception.alarm;

import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.common.exception.common.FloneyException;
import com.floney.floney.common.exception.common.LogType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GoogleAccessTokenGenerateException extends FloneyException {

    public GoogleAccessTokenGenerateException() {
        super(ErrorType.FAIL_TO_GENERATE_TOKEN,
                HttpStatus.INTERNAL_SERVER_ERROR,
                LogType.FAIL_TO_GENERATE_TOKEN);
    }
}
