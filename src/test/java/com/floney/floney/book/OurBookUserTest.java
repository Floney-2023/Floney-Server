package com.floney.floney.book;

import com.floney.floney.book.dto.OurBookUser;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class OurBookUserTest {

    @Test
    @DisplayName("방장이면 role이 방장으로 업데이트된다")
    void provider() {
        OurBookUser owner = BookFixture.createOurBookUser();
        owner.isProvider(BookFixture.EMAIL);
        Assertions.assertThat(owner.getRole()).isEqualTo("방장");
    }
}
