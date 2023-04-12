package com.floney.floney.user.dto.constant;

import lombok.Getter;

public enum Role {
    USER("ROLE_USER"),
    SUBSCRIBER("ROLE_SUBSCRIBER"),
    ADMIN("ROLE_ADMIN");

    @Getter
    private final String name;

    Role(String name) {
        this.name = name;
    }
}
