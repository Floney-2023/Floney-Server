package com.floney.floney.book;

import com.floney.floney.book.dto.constant.AssetType;
import com.floney.floney.book.entity.Book;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.floney.floney.book.BookFixture.BOOK_KEY;

public class BookLineTest {

    @Test
    @DisplayName("지출 내역일 경우 자산에서 해당 값을 뺀다")
    void outcome() {
        Long outcome = 1000L;
        Long budget = 2000L;
        Book book = Book.builder()
            .bookKey(BOOK_KEY)
            .budget(budget)
            .build();

        book.processTrans(AssetType.OUTCOME, outcome);
        Assertions.assertThat(book.getBudget())
            .isEqualTo(budget - outcome);
    }

    @Test
    @DisplayName("수입 내역일 경우 자산에서 해당 값을 더한다")
    void income() {
        Long income = 1000L;
        Long budget = 2000L;
        Book book = Book.builder()
            .bookKey(BOOK_KEY)
            .budget(budget)
            .build();

        book.processTrans(AssetType.INCOME, income);
        Assertions.assertThat(book.getBudget())
            .isEqualTo(budget + income);
    }

}
