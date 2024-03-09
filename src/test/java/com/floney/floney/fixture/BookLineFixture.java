package com.floney.floney.fixture;

import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookLine;
import com.floney.floney.book.domain.entity.BookLineCategory;
import com.floney.floney.book.domain.entity.BookUser;

import java.time.LocalDate;

public class BookLineFixture {

    public static final double DEFAULT_MONEY = 1000.0;
    public static final LocalDate DEFAULT_DATE = LocalDate.of(2000, 1, 1);

    public static BookLine create(final Book book,
                                  final BookUser bookUser,
                                  final BookLineCategory categories) {
        return BookLine.builder()
            .book(book)
            .writer(bookUser)
            .categories(categories)
            .lineDate(DEFAULT_DATE)
            .exceptStatus(false)
            .money(DEFAULT_MONEY)
            .build();
    }

    public static BookLine createWithDate(final Book book,
                                          final BookUser bookUser,
                                          final BookLineCategory categories,
                                          final LocalDate lineDate) {
        return BookLine.builder()
            .book(book)
            .writer(bookUser)
            .categories(categories)
            .lineDate(lineDate)
            .exceptStatus(false)
            .money(DEFAULT_MONEY)
            .build();
    }

    public static BookLine createWithDateNotBudget(final Book book,
                                                   final BookUser bookUser,
                                                   final BookLineCategory categories,
                                                   final LocalDate lineDate) {
        return BookLine.builder()
            .book(book)
            .writer(bookUser)
            .categories(categories)
            .lineDate(lineDate)
            .exceptStatus(true)
            .money(DEFAULT_MONEY)
            .build();
    }
}
