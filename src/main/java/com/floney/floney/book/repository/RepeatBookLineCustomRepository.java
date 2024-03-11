package com.floney.floney.book.repository;

import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookUser;

public interface RepeatBookLineCustomRepository {

    void inactiveAllByBook(Book book);

    void inactiveAllByBookUser(BookUser targetBookUser);
}
