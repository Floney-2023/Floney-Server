package com.floney.floney.common.exception.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NoAuthorityException extends FloneyException {

    public NoAuthorityException(String owner, String requestUser) {
        super(ErrorType.NO_AUTHORITY, HttpStatus.UNAUTHORIZED, ErrorLogType.NO_AUTHORITY);
        printLog(owner, ErrorType.NO_AUTHORITY.getMessage(), requestUser);
    }
}
