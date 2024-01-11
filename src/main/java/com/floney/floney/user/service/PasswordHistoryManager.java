package com.floney.floney.user.service;

public interface PasswordHistoryManager {

    void addPassword(String password, long userId);
}
