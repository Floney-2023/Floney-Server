package com.floney.floney.book.repository.analyze;

import com.floney.floney.book.domain.entity.Book;

public interface CarryOverCustomRepository {

    void inactiveAllByBookKey(String bookKey);

    void inactiveAllByBook(Book book);
}
