package com.floney.floney.common.exception.common;

import lombok.Getter;

@Getter
public class NoAuthorityException extends RuntimeException {
    private final ErrorType errorType;
    private final String owner;
    private final String requestUser;

    public NoAuthorityException(String owner, String requestUser) {
        errorType = ErrorType.NO_AUTHORITY;
        this.owner = owner;
        this.requestUser = requestUser;
    }
}
