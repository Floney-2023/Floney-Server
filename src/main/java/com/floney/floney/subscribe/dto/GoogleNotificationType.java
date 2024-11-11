package com.floney.floney.subscribe.dto;

public enum GoogleNotificationType {
    SUBSCRIPTION_CANCELED(3),

    SUBSCRIPTION_PURHCASED(4),
    SUBSCRIPTION_RESTARTED(7);

    private final int value;

    GoogleNotificationType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
