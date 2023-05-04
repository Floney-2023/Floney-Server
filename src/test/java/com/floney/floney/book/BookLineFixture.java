package com.floney.floney.book;

import com.floney.floney.book.dto.CreateLineRequest;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.BookLine;

import static com.floney.floney.book.BookFixture.BOOK_KEY;

public class BookLineFixture {

    public static final Long OUTCOME = 1000L;
    public static final Long INCOME = 1000L;

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

    public static BookLine createBookLine() {
        Book book = Book.builder()
            .bookKey(BOOK_KEY)
            .budget(2000L)
            .build();

        return BookLine.builder()
            .book(book)
            .build();
    }
}
