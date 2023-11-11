package com.floney.floney.book.repository.analyze;

import com.floney.floney.book.entity.Book;

public interface BudgetCustomRepository {

    void inactiveAllByBook(Book book);
}
