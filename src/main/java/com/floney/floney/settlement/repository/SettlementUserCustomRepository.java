package com.floney.floney.settlement.repository;

public interface SettlementUserCustomRepository {

    void inactiveAllByBookId(long bookId);
}
