package com.floney.floney.book.repository.analyze;

import com.floney.floney.book.domain.entity.Book;

import java.time.LocalDate;

public interface AssetCustomRepository {

    void inactiveAllByBook(Book book);

    void subtractMoneyByDateAndBook(double money, LocalDate date, Book book);
}
