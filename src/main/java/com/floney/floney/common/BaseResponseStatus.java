package com.floney.floney.common;

import lombok.Getter;

@Getter
public enum BaseResponseStatus {
    SUCCESS(true, 1000, "요청에 성공하였습니다."),

    // 9500: JWT
    AUTHENTICATION_FAIL(false, 9500, "사용자 인증에 실패했습니다."),
    USER_EXIST(false, 9501, "이미 가입 된 사용자입니다."),
    EXPIRED_TOKEN(false, 9502, "만료된 토큰입니다."),
    INVALID_TOKEN(false, 9503, "유효하지 않은 토큰입니다."),
    USER_NOT_EXIST(false, 9504, "사용자가 존재하지 않습니다.");

    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
