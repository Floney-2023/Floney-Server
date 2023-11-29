package com.floney.floney.book.util;


import java.util.UUID;

public class CodeFactory {

    private static final int START = 0;
    private static final int END = 8;

    public static String generateCode() {
        return UUID.randomUUID()
                .toString()
                .substring(START, END)
                .toUpperCase();
    }
}
