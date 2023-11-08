package com.floney.floney.fixture;

import com.floney.floney.book.dto.process.BookLineExpense;
import com.floney.floney.book.dto.request.ChangeBookLineRequest;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.BookLine;
import com.floney.floney.book.entity.BookUser;

import java.time.LocalDate;

import static com.floney.floney.fixture.BookFixture.BOOK_KEY;

public class BookLineFixture {

    public static final Long OUTCOME = 1000L;
    public static final Long INCOME = 1000L;

    public static LocalDate LOCAL_DATE = LocalDate.of(2023, 10, 22);

    public static ChangeBookLineRequest createOutcomeRequest() {
        return ChangeBookLineRequest.builder()
                .bookKey(BOOK_KEY)
                .flow("지출")
                .asset("은행")
                .line("식비")
                .money(OUTCOME)
                .build();
    }

    public static ChangeBookLineRequest createIncomeRequest() {
        return ChangeBookLineRequest.builder()
                .bookKey(BOOK_KEY)
                .flow("수입")
                .asset("은행")
                .line("월급")
                .money(OUTCOME)
                .build();
    }

    public static BookLine createBookLine(Book book, Long money) {
        BookLine bookline = BookLine.builder()
                .book(book)
                .money(money)
                .lineDate(LOCAL_DATE)
                .exceptStatus(Boolean.FALSE)
                .build();

        return bookline;
    }

    public static BookLine createBookLineWithWriter(Book book, Long money, BookUser writer) {
        BookLine bookline = BookLine.builder()
                .book(book)
                .money(money)
                .lineDate(LOCAL_DATE)
                .writer(writer)
                .exceptStatus(Boolean.FALSE)
                .build();

        return bookline;
    }


    public static BookLine createBookLineWith(BookUser user, Book book, Long money) {
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
                .money(1000L)
                .build();
    }
}
