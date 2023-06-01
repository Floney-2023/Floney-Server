package com.floney.floney.book;

import com.floney.floney.book.entity.Book;
import com.floney.floney.common.exception.NoAuthorityException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.floney.floney.book.BookFixture.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BookTest {

    @Test
    @DisplayName("가계부를 만든 사람이 아니면 삭제를 할 수 없다")
    void not_owner() {
        Book book = createBook();
        String other = "sienna011022@naver.com";

        assertThatThrownBy(() -> book.isOwner(other))
            .isInstanceOf(NoAuthorityException.class);
    }

    @Test
    @DisplayName("가계부 이미지를 변경한다")
    void update_img(){
        Book book = createBook();
        book.updateImg(updateBookImgRequest());
        Assertions.assertThat(book.getProfileImg())
            .isEqualTo(UPDATE_URL);
    }
}
