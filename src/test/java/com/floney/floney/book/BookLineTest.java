package com.floney.floney.book;

import com.floney.floney.book.entity.AssetType;
import com.floney.floney.book.entity.Book;
import com.floney.floney.common.exception.OutOfBudgetException;
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

    @Test
    @DisplayName("자산이 음수가 나올 경우 예외가 발생한다")
    void outcome_exception() {
        Long outcome = 3000L;
        Long budget = 2000L;
        Book book = Book.builder()
            .bookKey(BOOK_KEY)
            .budget(budget)
            .build();

        Assertions.assertThatThrownBy(() -> book.processTrans(AssetType.OUTCOME, outcome))
            .isInstanceOf(OutOfBudgetException.class);
    }

    @Test
    @DisplayName("자산이 999,999,999,999를 초과할 경우 예외가 발생한다")
    void income_exception() {
        Long income = 999999999L;
        Long budget = 2000L;
        Book book = Book.builder()
            .bookKey(BOOK_KEY)
            .budget(budget)
            .build();
        Assertions.assertThatThrownBy(() -> book.processTrans(AssetType.OUTCOME, income))
            .isInstanceOf(OutOfBudgetException.class);
    }

    @Test
    @DisplayName("자산의 최솟값은 0원이다")
    void budget_min() {
        Long outcome = 2000L;
        Long budget = 2000L;
        Book book = Book.builder()
            .bookKey(BOOK_KEY)
            .budget(budget)
            .build();


        book.processTrans(AssetType.OUTCOME, outcome);
        Assertions.assertThat(book.getBudget())
            .isEqualTo(0);
    }

    @Test
    @DisplayName("자산의 최대값은 999,999,999원이다")
    void budget_max() {
        Long income = 999999999L;
        Long budget = 0L;
        Book book = Book.builder()
            .bookKey(BOOK_KEY)
            .budget(budget)
            .build();

        book.processTrans(AssetType.INCOME, income);
        Assertions.assertThat(book.getBudget())
            .isEqualTo(999999999L);
    }
}
