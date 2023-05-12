package com.floney.floney.book;

import com.floney.floney.book.dto.BookLineExpense;
import com.floney.floney.book.dto.CalendarTotalExpense;
import com.floney.floney.book.entity.*;
import com.floney.floney.book.repository.BookLineCategoryRepository;
import com.floney.floney.book.repository.BookLineRepository;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.category.CategoryRepository;
import com.floney.floney.book.dto.constant.CategoryEnum;
import com.floney.floney.config.TestConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.Arrays;

import static com.floney.floney.book.BookFixture.BOOK_KEY;
import static com.floney.floney.book.BookLineFixture.*;
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
    private Category outcomeCategory;

    @BeforeEach
    void init() {
        book = bookRepository.save(BookFixture.createBook());
        incomeCategory = categoryRepository.save(incomeBookCategory());
        outcomeCategory = categoryRepository.save(outComeBookCategory());
    }

    @Test
    @DisplayName("각 가계부 내역 지정된 날짜기간의 수입/지출의 총합을 조회한다")
    void income() {
        BookLine bookLine = bookLineRepository.save(createBookLine(book, 1000L));
        BookLine bookLine2 = bookLineRepository.save(createBookLine(book, 1000L));

        BookLineCategory category = bookLineCategoryRepository.save(createLineCategory((DefaultCategory) incomeCategory, bookLine));
        bookLine.add(CategoryEnum.FLOW, category);

        BookLineCategory category2 = bookLineCategoryRepository.save(createLineCategory((DefaultCategory) outcomeCategory, bookLine2));
        bookLine2.add(CategoryEnum.FLOW, category2);

        bookLineRepository.save(bookLine);
        bookLineRepository.save(bookLine2);

        LocalDate start = LocalDate.of(2023, 10, 21);
        LocalDate end = LOCAL_DATE;

        BookLineExpense income = BookLineExpense.builder()
            .money(1000L)
            .assetType("수입")
            .date(LOCAL_DATE)
            .build();

        BookLineExpense outcome = BookLineExpense.builder()
            .money(1000L)
            .assetType("지출")
            .date(LOCAL_DATE)
            .build();

        Assertions.assertThat(bookLineRepository.dayIncomeAndOutcome(BOOK_KEY, start, end))
            .isEqualTo(Arrays.asList(income, outcome));

    }

    @Test
    @DisplayName("각 가계부 내역 지정된 달의 총수입/총지출을 조회한다")
    void all_expenses() {
        BookLine bookLine = bookLineRepository.save(createBookLine(book, 1000L));
        BookLine bookLine2 = bookLineRepository.save(createBookLine(book, 1000L));

        BookLineCategory category = bookLineCategoryRepository.save(createLineCategory((DefaultCategory) incomeCategory, bookLine));
        bookLine.add(CategoryEnum.FLOW, category);

        BookLineCategory category2 = bookLineCategoryRepository.save(createLineCategory((DefaultCategory) outcomeCategory, bookLine2));
        bookLine2.add(CategoryEnum.FLOW, category2);

        bookLineRepository.save(bookLine);
        bookLineRepository.save(bookLine2);

        LocalDate start = LocalDate.of(2023, 10, 1);
        LocalDate end = LOCAL_DATE;

        CalendarTotalExpense income = CalendarTotalExpense.builder()
            .money(1000L)
            .assetType("수입")
            .build();

        CalendarTotalExpense outcome = CalendarTotalExpense.builder()
            .money(1000L)
            .assetType("지출")
            .build();

        Assertions.assertThat(bookLineRepository.totalExpense(BOOK_KEY, start, end))
            .isEqualTo(Arrays.asList(income, outcome));

    }
}


