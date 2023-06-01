package com.floney.floney.book;

import com.floney.floney.book.entity.Book;
import com.floney.floney.common.exception.NoAuthorityException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BookTest {

    @Test
    @DisplayName("가계부를 만든 사람이 아니면 삭제를 할 수 없다")
    void not_owner() {
        Book book = BookFixture.createBook();
        String other = "sienna011022@naver.com";

        assertThatThrownBy(() -> book.isOwner(other))
            .isInstanceOf(NoAuthorityException.class);
    }
}
