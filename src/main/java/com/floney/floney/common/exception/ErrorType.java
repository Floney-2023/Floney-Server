package com.floney.floney.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorType {

    NOT_FOUND_BOOK("B001", "가계부가 존재하지 않습니다"),
    MAX_MEMBER("B002", "최대 인원이 초과되었습니다"),
    NOT_FOUND_CATEGORY("B003", "카테고리가 존재하지 않습니다");

    private final String code;
    private final String message;

}

