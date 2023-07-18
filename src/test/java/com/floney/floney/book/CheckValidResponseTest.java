package com.floney.floney.book;

import com.floney.floney.book.dto.response.CheckBookValidResponse;
import com.floney.floney.book.entity.Book;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CheckValidResponseTest {

    @Test
    @DisplayName("내가 속한 가계부가 존재하면, 가계부 키를 응답한다")
    void valid_book() {
        Book book = BookFixture.createBook();
        Assertions.assertThat(CheckBookValidResponse.userBook(book).getBookKey()).isEqualTo(book.getBookKey());
    }

    @Test
    @DisplayName("내가 속한 가계부가 존재하지 않으면, 가계부 키를 null로 응답한다")
    void invalid_book() {
        Assertions.assertThat(CheckBookValidResponse.userBook(Book.initBook()).getBookKey())
            .isEqualTo(null);
    }
}
