package com.floney.floney.fixture;

import com.floney.floney.book.domain.entity.*;

import java.time.LocalDate;

public class BookLineFixture {

    public static final double DEFAULT_MONEY = 1000.0;

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

    public static BookLine createWithDateAndRepeat(final Book book,
                                                   final BookUser bookUser,
                                                   final BookLineCategory categories,
                                                   final RepeatBookLine repeatBookLine,
                                                   final LocalDate lineDate) {
        return BookLine.builder()
            .book(book)
            .writer(bookUser)
            .categories(categories)
            .lineDate(lineDate)
            .exceptStatus(false)
            .money(DEFAULT_MONEY)
            .repeatBookLine(repeatBookLine)
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
