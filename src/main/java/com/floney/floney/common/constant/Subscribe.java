package com.floney.floney.common.constant;

import lombok.Getter;

@Getter
public enum Subscribe {
    SUBSCRIBE_MAX_MEMBER(4),
    SUBSCRIBE_MAX_BOOK(2),
    DEFAULT_MAX_MEMBER(2),
    DEFAULT_MAX_BOOK(1);

    private final int value;
    Subscribe(int value) {
        this.value = value;
    }
}
