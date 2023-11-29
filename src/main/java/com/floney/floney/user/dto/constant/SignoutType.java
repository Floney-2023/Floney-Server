package com.floney.floney.user.dto.constant;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SignoutType {

    OTHER("기타"),
    NOT_KNOW_HOW_TO_USE("플로니를 어떻게 사용하는지 모르겠어요"),
    EXPENSIVE("구독료가 비싸요"),
    NOT_USE_OFTEN("자주 사용하지 않게 돼요"),
    WILL_SIGNUP_AGAIN("다시 가입할 거예요");

    private final String value;
}
