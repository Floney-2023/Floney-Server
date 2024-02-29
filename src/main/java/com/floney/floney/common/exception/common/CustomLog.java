package com.floney.floney.common.exception.common;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomLog {

    private final CustomLogLevel level;
    private final String message;

    public static CustomLog of(CustomLogLevel level, String message) {
        return new CustomLog(level, message);
    }

    public String getMessage() {
        return message;
    }
}
