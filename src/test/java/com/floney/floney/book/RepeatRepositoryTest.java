package com.floney.floney.book;

import com.floney.floney.book.entity.RepeatCategory;
import com.floney.floney.book.repository.RepeatCategoryRepository;
import com.floney.floney.config.TestConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig.class)
public class RepeatRepositoryTest {

    @Autowired
    private RepeatCategoryRepository repeatRepository;

    @Test
    @DisplayName("반복 설정을 저장하고 조회한다")
    void save_and_find() {
        String testKind = "1달";
        RepeatCategory newRepeat = RepeatCategory.builder()
            .kind(testKind)
            .build();

        repeatRepository.save(newRepeat);

        Optional<RepeatCategory> savedCategory = repeatRepository.findRepeatCategoryByKind(testKind);
        Assertions.assertThat(savedCategory.get().getKind())
            .isEqualTo(testKind);
    }

}
