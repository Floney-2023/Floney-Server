package com.floney.floney.book.dto.constant;

public enum DayType {
    ONE_DAY(1),
    ONE_MONTH(1),
    THREE_MONTH(3),
    FIRST_DAY(1),
    FIVE_MOTH(5),
    FIVE_YEAR(5),
    FIVE_YEAR_TO_DAY(60);

    private final int value;

    DayType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
