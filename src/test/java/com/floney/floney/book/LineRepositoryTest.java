package com.floney.floney.book;

import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.LineCategory;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.LineCategoryRepository;
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
public class LineRepositoryTest {
    @Autowired
    private LineCategoryRepository lineRepository;
    @Autowired
    private BookRepository bookRepository;

    Book savedBook;

    @BeforeEach
    void init() {
        savedBook = bookRepository.save(BookFixture.createBookWith(1L));
    }

    @Test
    @DisplayName("분류를 저장하고 조회한다")
    void save_and_read() {
        String testLine = "식비";
        LineCategory newLine = LineCategory.builder()
            .bookId(savedBook)
            .line(testLine)
            .build();
        lineRepository.save(newLine);
        Optional<LineCategory> lineCategory = lineRepository.findLineCategoryByLine(testLine);
        Assertions.assertThat(lineCategory.get().getLine()).isEqualTo(testLine);
    }
}
