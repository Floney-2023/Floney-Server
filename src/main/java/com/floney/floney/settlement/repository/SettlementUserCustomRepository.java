package com.floney.floney.settlement.repository;

import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.user.entity.User;

public interface SettlementUserCustomRepository {

    void inactiveAllByBookId(long bookId);

    void inactiveAllByBookAndUser(final Book book, User user);
}
