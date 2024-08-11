package com.floney.floney.book.repository;

import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.Budget;
import com.floney.floney.book.repository.analyze.BudgetRepository;
import com.floney.floney.config.QueryDslTest;
import com.floney.floney.fixture.BookFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@QueryDslTest
@DisplayName("단위 테스트: BookCustomRepository")
class BudgetCustomRepositoryTest {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private BookRepository bookRepository;

    @Nested
    @DisplayName("deleteAllBy()를 실행할 때")
    class Describe_DeleteAllBy {

        @Nested
        @DisplayName("가계부 budget이 존재하는 경우")
        class Context_With_Budget {

            final Book book = BookFixture.createBook();
            Book savedBook;

            @BeforeEach
            void init() {
                savedBook = bookRepository.save(book);
                budgetRepository.save(new Budget(LocalDate.now(), 1000.0, savedBook));
                budgetRepository.save(new Budget(LocalDate.of(2024, 9, 1), 3000.0, savedBook));
            }

            @Test
            @DisplayName("모든 budget을 삭제한다.")
            void it_returns_book() {
                budgetRepository.deleteAllBy(savedBook);
                assertThat(budgetRepository.findAll()).isEmpty();
            }
        }
    }
}
