package com.floney.floney.settlement.repository;

public interface SettlementCustomRepository {

    void inactiveAllByBookKey(String bookKey);

    void inactiveAllByBookId(long bookId);
}
