package com.floney.floney.fixture;

import com.floney.floney.book.dto.process.BookLineExpense;
import com.floney.floney.book.dto.request.BookLineRequest;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookLine;
import com.floney.floney.book.domain.entity.BookUser;

import java.time.LocalDate;

import static com.floney.floney.fixture.BookFixture.BOOK_KEY;

public class BookLineFixture {

    public static final double OUTCOME = 1000.0;
    public static final double INCOME = 1000.0;

    public static LocalDate LOCAL_DATE = LocalDate.of(2023, 10, 22);

    public static BookLineRequest createOutcomeRequest() {
        return BookLineRequest.builder()
                .bookKey(BOOK_KEY)
                .flow("지출")
                .asset("은행")
                .line("식비")
                .money(OUTCOME)
                .build();
    }

    public static BookLineRequest createIncomeRequest() {
        return BookLineRequest.builder()
                .bookKey(BOOK_KEY)
                .flow("수입")
                .asset("은행")
                .line("월급")
                .money(OUTCOME)
                .build();
    }

    public static BookLine createBookLine(Book book, double money) {
        BookLine bookline = BookLine.builder()
                .book(book)
                .money(money)
                .lineDate(LOCAL_DATE)
                .exceptStatus(Boolean.FALSE)
                .build();

        return bookline;
    }

    public static BookLine createBookLineWithWriter(Book book, double money, BookUser writer) {
        BookLine bookline = BookLine.builder()
                .book(book)
                .money(money)
                .lineDate(LOCAL_DATE)
                .writer(writer)
                .exceptStatus(Boolean.FALSE)
                .build();

        return bookline;
    }


    public static BookLine createBookLineWith(BookUser user, Book book, double money) {
        BookLine bookline = BookLine.builder()
                .book(book)
                .writer(user)
                .money(money)
                .lineDate(LOCAL_DATE)
                .exceptStatus(Boolean.FALSE)
                .build();

        return bookline;
    }

    public static BookLineExpense createBookLineExpense() {
        return BookLineExpense.builder()
                .assetType("수입")
                .date(LOCAL_DATE)
                .money(1000f)
                .build();
    }
}
