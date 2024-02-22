package com.floney.floney.book.domain;

public enum RepeatDuration {

    EVERYDAY("매일"),
    WEEK("매주"),
    MONTH("매달"),
    WEEKDAY("주중"),
    WEEKEND("주말");

    private String meaning;

    RepeatDuration(final String meaning) {
        this.meaning = meaning;
    }
}
