package com.floney.floney.common.exception.common;

public class LogFactory {

    private LogFactory() {
        throw new IllegalStateException();
    }

    public static String createErrorLog(String logPattern, String[] logAttributes) {
        return String.format(logPattern, (Object[]) logAttributes);
    }
}
