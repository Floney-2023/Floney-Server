package com.floney.floney.settlement.repository;

import com.floney.floney.book.domain.entity.Book;

public interface SettlementCustomRepository {

    void inactiveAllBy(Book book);

    void inactiveAllByBookId(long bookId);
}
