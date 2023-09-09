package com.floney.floney.common.constant;

import lombok.Getter;

@Getter
public enum Subscribe {
    S_MAX_MEMBER (4),
    S_MAX_BOOK(2);

    private final int value;
    Subscribe(int value) {
        this.value = value;
    }
}
