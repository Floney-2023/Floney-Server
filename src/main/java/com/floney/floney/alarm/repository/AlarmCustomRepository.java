package com.floney.floney.alarm.repository;

import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookUser;

public interface AlarmCustomRepository {

    void inactiveAllByBookUser(final BookUser bookUser);

    void inactiveAllByBook(final Book book);
}
