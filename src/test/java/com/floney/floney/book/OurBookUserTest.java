package com.floney.floney.book;

import com.floney.floney.book.dto.process.OurBookUser;
import com.floney.floney.fixture.BookFixture;
import com.floney.floney.fixture.UserFixture;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class OurBookUserTest {

    @Test
    @DisplayName("방장이면 role이 방장으로 업데이트된다")
    void provider() {
        OurBookUser owner = BookFixture.createOurBookUser();
        owner.checkRole(UserFixture.DEFAULT_EMAIL);
        Assertions.assertThat(owner.getRole()).isEqualTo("방장");
    }

    @Test
    @DisplayName("내 계정을 me의 True로 표시한다")
    void isMe() {
        OurBookUser owner = BookFixture.createOurBookUser();
        owner.isMyAccount(UserFixture.DEFAULT_EMAIL);
        Assertions.assertThat(owner.isMe()).isEqualTo(true);
    }
}
