package com.floney.floney.fixture;

import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookLine;
import com.floney.floney.book.domain.entity.BookUser;
import com.floney.floney.book.dto.process.BookLineExpense;
import com.floney.floney.book.dto.request.BookLineRequest;

import java.time.LocalDate;

import static com.floney.floney.fixture.BookFixture.BOOK_KEY;

public class BookLineFixture {

    public static final String OUTCOME = "지출";
    public static final String INCOME = "수입";

    public static LocalDate DEFAULT_DATE = LocalDate.of(2023, 10, 22);

    public static BookLineRequest outcomeRequest(final double money) {
        return BookLineRequest.builder()
                .bookKey(BOOK_KEY)
                .flow(OUTCOME)
                .asset("은행")
                .line("식비")
                .money(money)
                .build();
    }

    public static BookLineRequest incomeRequest(final double money) {
        return BookLineRequest.builder()
                .bookKey(BOOK_KEY)
                .flow(INCOME)
                .asset("은행")
                .line("월급")
                .money(money)
                .build();
    }

    public static BookLine createBookLineWithWriter(Book book, double money, BookUser writer) {
        return BookLine.builder()
                .book(book)
                .money(money)
                .lineDate(DEFAULT_DATE)
                .writer(writer)
                .exceptStatus(Boolean.FALSE)
                .build();
    }

    public static BookLineExpense createBookLineExpense() {
        return BookLineExpense.builder()
                .assetType(INCOME)
                .date(DEFAULT_DATE)
                .money(1000.0)
                .build();
    }
}
