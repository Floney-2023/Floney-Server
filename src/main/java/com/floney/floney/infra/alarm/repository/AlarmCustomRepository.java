package com.floney.floney.infra.alarm.repository;

import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.BookUser;

public interface AlarmCustomRepository {

    void inactiveAllByBookUser(final BookUser bookUser);

    void inactiveAllByBook(final Book book);
}
