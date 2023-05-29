package com.floney.floney.book;

import com.floney.floney.book.dto.BookLineExpense;
import com.floney.floney.book.dto.MonthLinesResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class MonthLineResponseTest {

    @Test
    void response() {
        List<BookLineExpense> savedExpense = Arrays.asList(BookLineFixture.createBookLineExpense());
        List<BookLineExpense> saved = MonthLinesResponse.reflectDB("2023-10-01", savedExpense);
        Assertions.assertThat(saved.size()).isEqualTo(31);
    }
}
