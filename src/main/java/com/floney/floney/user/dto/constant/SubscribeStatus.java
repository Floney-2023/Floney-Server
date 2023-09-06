package com.floney.floney.user.dto.constant;

import java.util.Objects;

public enum SubscribeStatus {
    ACTIVE("active"),
    EXPIRED("expired"),
    CANCELED("canceled");

    private final String status;

    SubscribeStatus(String status) {
        this.status = status;
    }

    public static boolean isExpired(String status) {
        return Objects.equals(EXPIRED.status, status);
    }
}
