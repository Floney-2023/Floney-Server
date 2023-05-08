package com.floney.floney.book;

import com.floney.floney.book.dto.CreateLineRequest;
import com.floney.floney.book.entity.*;

import java.time.LocalDate;

import static com.floney.floney.book.BookFixture.BOOK_KEY;

public class BookLineFixture {

    public static final Long OUTCOME = 1000L;
    public static final Long INCOME = 1000L;

    public static LocalDate LOCAL_DATE = LocalDate.of(2023,10,22);

    public static CreateLineRequest createOutcomeRequest() {
        return CreateLineRequest.builder()
            .bookKey(BOOK_KEY)
            .asset("지출")
            .line("식비")
            .money(OUTCOME)
            .build();
    }

    public static CreateLineRequest createIncomeRequest() {
        return CreateLineRequest.builder()
            .bookKey(BOOK_KEY)
            .asset("수입")
            .line("월급")
            .money(OUTCOME)
            .build();
    }

    public static BookLine createBookLine(Book book, Long money) {
        BookLine bookline = BookLine.builder()
            .book(book)
            .money(money)
            .lineDate(LOCAL_DATE)
            .build();

        return bookline;
    }
}
