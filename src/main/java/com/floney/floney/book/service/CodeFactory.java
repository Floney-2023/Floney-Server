package com.floney.floney.book.service;


import java.util.UUID;

public class CodeFactory {
    public static String generateCode() {
        return UUID.randomUUID().toString();
    }
}
