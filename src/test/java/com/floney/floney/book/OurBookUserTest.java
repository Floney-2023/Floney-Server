package com.floney.floney.book;

import com.floney.floney.book.dto.OurBookInfo;
import com.floney.floney.book.dto.OurBookUser;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static com.floney.floney.book.BookFixture.EMAIL;
import static org.assertj.core.util.Arrays.asList;

public class OurBookUserTest {

    @Test
    @DisplayName("방장이면 role이 방장으로 업데이트된다")
    void provider() {
        OurBookUser owner = BookFixture.createOurBookUser();
        owner.checkRole(EMAIL);
        Assertions.assertThat(owner.getRole()).isEqualTo("방장");
    }

    @Test
    @DisplayName("내 계정을 isMe True로 표시한다")
    void isMe() {
        OurBookUser owner = BookFixture.createOurBookUser();
        owner.isMyAccount(EMAIL);
        Assertions.assertThat(owner.isMe()).isEqualTo(true);
    }
}
