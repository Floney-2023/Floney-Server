package com.floney.floney.book;

import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.FlowCategory;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.FlowCategoryRepository;
import com.floney.floney.config.TestConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
public class FlowRepositoryTest {

    @Autowired
    private FlowCategoryRepository flowRepository;
    @Autowired
    private BookRepository bookRepository;

    Book savedBook;

    @BeforeEach
    void init() {
        savedBook = bookRepository.save(BookFixture.createBookWith(1L));
    }

    @Test
    @DisplayName("종류카테고리를 추가하고 조회한다")
    void save_read() {
        String testFlow = "이체";
        FlowCategory newFlow = FlowCategory.builder()
            .bookId(savedBook)
            .flow(testFlow)
            .build();

        flowRepository.save(newFlow);

        Optional<FlowCategory> savedFlow = flowRepository.findFlowCategoryByFlow(testFlow);

        Assertions.assertThat(savedFlow.get().getFlow())
            .isEqualTo(testFlow);
    }
}
