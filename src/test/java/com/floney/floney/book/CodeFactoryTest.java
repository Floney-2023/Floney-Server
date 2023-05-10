package com.floney.floney.book;

import com.floney.floney.book.util.CodeFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Objects;

@Disabled
public class CodeFactoryTest {
    @Test
    @DisplayName("랜덤 초대코드를 발행한다")
    void create_code() {
        String code1 = CodeFactory.generateCode();
        String code2 = CodeFactory.generateCode();

        Assertions.assertThat(Objects.equals(code1, code2)).isFalse();

    }
}
