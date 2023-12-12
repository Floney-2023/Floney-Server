package com.floney.floney.book.repository.analyze;

import com.floney.floney.book.domain.entity.Book;

import java.time.LocalDate;

public interface AssetCustomRepository {

    void inActiveAllByBook(Book book);

    void updateMoneyByDateAndBook(double money, LocalDate date, Book book);
}
