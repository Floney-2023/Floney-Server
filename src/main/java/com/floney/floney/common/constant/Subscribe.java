package com.floney.floney.common.constant;

import lombok.Getter;

@Getter
public enum Subscribe {
    DEFAULT_MAX_MEMBER(4),
    DEFAULT_MAX_BOOK(2);

    private final int value;

    Subscribe(int value) {
        this.value = value;
    }
}
