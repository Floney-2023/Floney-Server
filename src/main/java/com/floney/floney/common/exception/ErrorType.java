package com.floney.floney.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorType {

    USER_FOUND("U001", "이미 존재하는 유저입니다"),
    NOT_AUTHENTICATED("U002", "로그인이 필요합니다"),
    INVALID_MAIL_ADDRESS("U003", "메일 주소가 올바르지 않습니다"),
    FAIL_TO_SEND_MAIL("U004", "메일을 보내는 도중 오류가 발생했습니다"),
    INVALID_AUTHENTICATION("U005", "올바르지 않은 인증 요청 입니다"),
    EXPIRED_JWT_TOKEN("U006", "만료된 토큰입니다"),
    INVALID_JWT_TOKEN("U007", "올바르지 않은 토큰입니다"),
    USER_NOT_FOUND("U008", "해당 이메일로 가입된 유저가 없습니다"),
    INVALID_LOGIN("U009", "잘못된 정보로 로그인에 실패했습니다"),
    USER_SIGNOUT("U010", "탈퇴한 회원입니다"),

    NOT_FOUND_BOOK("B001", "가계부가 존재하지 않습니다"),
    MAX_MEMBER("B002","최대 인원이 초과되었습니다");

    private final String code;
    private final String message;

}

