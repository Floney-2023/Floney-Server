package com.floney.floney.user.dto.constant;

import lombok.Getter;

@Getter
public enum Role {

    USER("ROLE_USER", 0),
    SUBSCRIBER("ROLE_SUBSCRIBER", 1),
    ADMIN("ROLE_ADMIN", 2);

    private final String name;
    private final int status;

    Role(String name, int status) {
        this.name = name;
        this.status = status;
    }
}
