package com.floney.floney.book;

import com.floney.floney.book.domain.Currency;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.common.exception.common.NoAuthorityException;
import com.floney.floney.fixture.BookFixture;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.floney.floney.fixture.BookFixture.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BookTest {

    @Test
    @DisplayName("가계부를 만든 사람이 아니면 삭제를 할 수 없다")
    void not_owner() {
        Book book = createBook();
        String other = "sienna011022@naver.com";

        assertThatThrownBy(() -> book.validateOwner(other))
            .isInstanceOf(NoAuthorityException.class);
    }

    @Test
    @DisplayName("가계부 이미지를 변경한다")
    void update_img() {
        Book book = createBook();
        book.updateImg(updateBookImgRequest());
        Assertions.assertThat(book.getBookImg())
            .isEqualTo(UPDATE_URL);
    }

    @Test
    @DisplayName("화폐설정을 변경한다")
    void change_currency() {
        Currency changeTo = Currency.CNY;
        Book book = BookFixture.createBook();
        book.changeCurrency(changeTo);
        Assertions.assertThat(book.getCurrency()).isEqualTo(changeTo.toString());
    }

}
