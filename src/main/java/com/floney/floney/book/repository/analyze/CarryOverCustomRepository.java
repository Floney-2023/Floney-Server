package com.floney.floney.book.repository.analyze;

import com.floney.floney.book.domain.entity.Book;

public interface CarryOverCustomRepository {

    void inactiveAllBy(Book book);
}
