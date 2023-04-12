package com.floney.floney.user.dto.constant;

import lombok.Getter;

public enum Provider {
    EMAIL("email"),
    KAKAO("kakao"),
    GOOGLE("google"),
    APPLE("apple");

    @Getter
    private final String name;

    Provider(String name) {
        this.name = name;
    }
}
