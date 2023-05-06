package com.floney.floney.book;

import com.floney.floney.book.entity.*;
import com.floney.floney.book.repository.BookLineCategoryRepository;
import com.floney.floney.book.repository.BookLineRepository;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.category.CategoryRepository;
import com.floney.floney.book.service.CategoryEnum;
import com.floney.floney.config.TestConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static com.floney.floney.book.BookFixture.BOOK_KEY;
import static com.floney.floney.book.BookLineFixture.LOCAL_DATE;
import static com.floney.floney.book.BookLineFixture.createIncomeLine;
import static com.floney.floney.book.CategoryFixture.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig.class)
public class BookLineRepositoryTest {

    @Autowired
    private BookLineRepository bookLineRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookLineCategoryRepository bookLineCategoryRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    private Book book;
    private Category incomeCategory;

    @BeforeEach
    void init() {
        book = bookRepository.save(BookFixture.createBook());
        incomeCategory = categoryRepository.save(incomeBookCategory());
    }

    @Test
    @DisplayName("하루의 수입 총합을 조회한다")
    void income() {
        BookLine bookLine = bookLineRepository.save(createIncomeLine(book, 1000L));
        BookLine bookLine2 = bookLineRepository.save(createIncomeLine(book, 2000L));

        BookLineCategory category = bookLineCategoryRepository.save(createIncomeLineCategory((DefaultCategory) incomeCategory, bookLine));
        bookLine.add(CategoryEnum.FLOW, category);

        BookLineCategory category2 = bookLineCategoryRepository.save(createIncomeLineCategory((DefaultCategory) incomeCategory, bookLine2));
        bookLine2.add(CategoryEnum.FLOW, category2);

        bookLineRepository.save(bookLine);
        bookLineRepository.save(bookLine2);

        Assertions.assertThat(bookLineRepository.dayIncome(BOOK_KEY, LOCAL_DATE))
            .isEqualTo(3000L);
    }
}


