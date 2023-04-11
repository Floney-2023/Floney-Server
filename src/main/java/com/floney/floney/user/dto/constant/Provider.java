package com.floney.floney.user.dto.constant;

import lombok.Getter;

public enum Provider {
    EMAIL("EMAIL"),
    KAKAO("KAKAO"),
    GOOGLE("GOOGLE"),
    APPLE("APPLE");

    @Getter
    private final String name;

    Provider(String name) {
        this.name = name;
    }
}
