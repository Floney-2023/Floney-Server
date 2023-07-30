package com.floney.floney.book;

import com.floney.floney.book.entity.BookAnalyze;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.Category;
import com.floney.floney.book.repository.BookAnalyzeRepository;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.category.CategoryRepository;
import com.floney.floney.config.TestConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;

import static com.floney.floney.book.BookFixture.BOOK_KEY;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig.class)
public class BookAnalyzeRepositoryTest {

    @Autowired
    private BookAnalyzeRepository bookAnalyzeRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("특정 달의 가계부 분석을 조회한다")
    void analyze() {
        Book book = bookRepository.save(BookFixture.createBook());
        Category category = categoryRepository.save(CategoryFixture.createDefaultRoot("수입"));

        BookAnalyze analyze = BookAnalyze.builder()
            .book(book)
            .category(category)
            .totalMoney(10L)
            .analyzeDate(LocalDate.of(2023, 5, 1))
            .build();

        bookAnalyzeRepository.save(analyze);
        BookAnalyze saved = bookAnalyzeRepository.findAnalyze(BOOK_KEY,LocalDate.of(2023, 5, 1));
        Assertions.assertThat(saved.getTotalMoney()).isEqualTo(10L);
    }
}
