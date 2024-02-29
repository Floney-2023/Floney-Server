package com.floney.floney.common.exception.common;

import lombok.Getter;

@Getter
public class NoAuthorityException extends FloneyException {

    private static final String LOG_PATTERN = "%s : 가계부 방장 [%s] , 요청자 [%s]";

    public NoAuthorityException(String owner, String requestUser) {
        super(ErrorType.NO_AUTHORITY, LOG_PATTERN, owner, ErrorType.NO_AUTHORITY.getMessage(), requestUser);
    }
}
