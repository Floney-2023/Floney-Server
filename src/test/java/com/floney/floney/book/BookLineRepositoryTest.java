package com.floney.floney.book;

import com.floney.floney.analyze.dto.response.AnalyzeResponseByCategory;
import com.floney.floney.book.domain.entity.*;
import com.floney.floney.book.domain.entity.category.BookCategory;
import com.floney.floney.book.dto.constant.CategoryEnum;
import com.floney.floney.book.dto.process.BookLineExpense;
import com.floney.floney.book.dto.process.DatesDuration;
import com.floney.floney.book.dto.process.TotalExpense;
import com.floney.floney.book.dto.request.AllOutcomesRequest;
import com.floney.floney.book.repository.BookLineRepository;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.book.repository.category.BookLineCategoryRepository;
import com.floney.floney.book.repository.category.CategoryRepository;
import com.floney.floney.config.TestConfig;
import com.floney.floney.fixture.BookFixture;
import com.floney.floney.fixture.UserFixture;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.UserRepository;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.floney.floney.book.CategoryFixture.*;
import static com.floney.floney.fixture.BookFixture.*;
import static com.floney.floney.fixture.BookLineFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig.class)
public class BookLineRepositoryTest {

    @Autowired
    private BookLineRepository bookLineRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookUserRepository bookUserRepository;
    @Autowired
    private BookLineCategoryRepository bookLineCategoryRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    private Book book;
    private User user;
    private BookUser bookUser;

    private Category incomeCategory;
    private Category outcomeCategory;

    private BookCategory childIncomeCategory;

    @BeforeEach
    void init() {
        user = userRepository.save(UserFixture.createUser());
        book = bookRepository.save(BookFixture.createBook());
        bookUser = bookUserRepository.save(BookUser.of(user, book));
        incomeCategory = categoryRepository.save(incomeBookCategory());
        outcomeCategory = categoryRepository.save(outComeBookCategory());
        childIncomeCategory = categoryRepository.save(createChildCategory(incomeCategory, book));
    }

    @Test
    @DisplayName("각 가계부 내역 지정된 기간의 수입/지출의 총합을 조회한다")
    void income() {
        /* given */
        final BookLine bookLine = bookLineRepository.save(createBookLineWithWriter(book, 1000.0, bookUser));
        final BookLine bookLine2 = bookLineRepository.save(createBookLineWithWriter(book, 1000.0, bookUser));

        final BookLineCategory category = bookLineCategoryRepository.save(createFlowCategory((DefaultCategory) incomeCategory, bookLine));
        bookLine.add(CategoryEnum.FLOW, category);

        final BookLineCategory category2 = bookLineCategoryRepository.save(createFlowCategory((DefaultCategory) outcomeCategory, bookLine2));
        bookLine2.add(CategoryEnum.FLOW, category2);

        final DatesDuration dates = DatesDuration.builder()
                .startDate(LOCAL_DATE.minusDays(1))
                .endDate(LOCAL_DATE)
                .build();

        /* when */
        final List<BookLineExpense> result = bookLineRepository.dayIncomeAndOutcome(BOOK_KEY, dates);

        /* then */
        final BookLineExpense income = BookLineExpense.builder()
                .money(1000.0)
                .assetType("수입")
                .date(LOCAL_DATE)
                .build();
        final BookLineExpense outcome = BookLineExpense.builder()
                .money(1000.0)
                .assetType("지출")
                .date(LOCAL_DATE)
                .build();

        assertThat(result).containsExactlyInAnyOrder(income, outcome);
    }

    @Test
    @DisplayName("각 가계부 내역 지정된 달의 총수입/총지출을 조회한다")
    void all_expenses() {
        /* given */
        final BookLine bookLine = bookLineRepository.save(createBookLineWithWriter(book, 1000.0, bookUser));
        final BookLine bookLine2 = bookLineRepository.save(createBookLineWithWriter(book, 1000.0, bookUser));

        final BookLineCategory category = bookLineCategoryRepository.save(createFlowCategory((DefaultCategory) incomeCategory, bookLine));
        bookLine.add(CategoryEnum.FLOW, category);

        final BookLineCategory category2 = bookLineCategoryRepository.save(createFlowCategory((DefaultCategory) outcomeCategory, bookLine2));
        bookLine2.add(CategoryEnum.FLOW, category2);

        final DatesDuration dates = DatesDuration.builder()
                .startDate(LocalDate.of(2023, 10, 1))
                .endDate(LOCAL_DATE)
                .build();

        /* when */
        final Map<String, Double> result = bookLineRepository.totalExpenseByMonth(BOOK_KEY, dates);

        /* then */
        assertThat(result).hasSize(2)
                .hasEntrySatisfying(
                        "수입",
                        income -> assertThat(income).isCloseTo(1000.0, Percentage.withPercentage(99.9))
                )
                .hasEntrySatisfying(
                        "지출",
                        outcome -> assertThat(outcome).isCloseTo(1000.0, Percentage.withPercentage(99.9))
                );
    }

    @Test
    @DisplayName("날짜별로 총수입/총지출을 조회한다")
    void day_expenses() {
        /* given */
        final BookLine bookLine = bookLineRepository.save(createBookLineWithWriter(book, 1000.0, bookUser));
        final BookLine bookLine2 = bookLineRepository.save(createBookLineWithWriter(book, 1000.0, bookUser));

        final BookLineCategory category = bookLineCategoryRepository.save(createFlowCategory((DefaultCategory) incomeCategory, bookLine));
        bookLine.add(CategoryEnum.FLOW, category);

        final BookLineCategory category2 = bookLineCategoryRepository.save(createFlowCategory((DefaultCategory) outcomeCategory, bookLine2));
        bookLine2.add(CategoryEnum.FLOW, category2);

        /* when */
        final List<TotalExpense> result = bookLineRepository.totalExpenseByDay(LOCAL_DATE, BOOK_KEY);

        /* then */
        final TotalExpense income = TotalExpense.builder()
                .money(1000.0)
                .assetType("수입")
                .build();
        final TotalExpense outcome = TotalExpense.builder()
                .money(1000.0)
                .assetType("지출")
                .build();

        assertThat(result).containsExactlyInAnyOrder(income, outcome);
    }

    @Test
    @DisplayName("날짜 별로 가계부 내역과 연관된 모든 카테고리와 금액을 조회한다")
    void days_line() {
        BookUser bookUser = bookUserRepository.save(createBookUser(user, book));
        BookLine bookLine = bookLineRepository.save(createBookLineWith(bookUser, book, 1000.0));
        BookLine bookLine2 = bookLineRepository.save(createBookLineWith(bookUser, book, 1000.0));

        BookLineCategory incomeBookLineCategory = bookLineCategoryRepository.save(createFlowCategory((DefaultCategory) incomeCategory, bookLine));
        BookLineCategory childLineCategory = bookLineCategoryRepository.save(createChildLineCategory(childIncomeCategory, bookLine));
        bookLine.add(CategoryEnum.FLOW, incomeBookLineCategory);
        bookLine.add(CategoryEnum.FLOW_LINE, childLineCategory);

        BookLineCategory bookLineCategory2 = bookLineCategoryRepository.save(createFlowCategory((DefaultCategory) outcomeCategory, bookLine2));
        bookLine2.add(CategoryEnum.FLOW, bookLineCategory2);

        bookLineRepository.save(bookLine);
        bookLineRepository.save(bookLine2);

        LocalDate targetDate = LOCAL_DATE;
        assertThat(bookLineRepository.allLinesByDay(targetDate, BOOK_KEY).size()).isEqualTo(3);
    }

    @Test
    @DisplayName("정산에 참여하는 유저가 사용한 내역만 기간 내에 조회")
    void all_dates_with_bookUsers() {
        BookUser bookUser = bookUserRepository.save(BookFixture.createBookUser(user, book));

        BookLine bookLine = bookLineRepository.save(createBookLineWithWriter(book, 1000.0, bookUser));
        BookLine bookLine2 = bookLineRepository.save(createBookLineWithWriter(book, 1000.0, bookUser));

        BookLineCategory incomeBookLineCategory = bookLineCategoryRepository.save(createFlowCategory((DefaultCategory) incomeCategory, bookLine));
        bookLine.add(CategoryEnum.FLOW, incomeBookLineCategory);

        BookLineCategory bookLineCategory2 = bookLineCategoryRepository.save(createFlowCategory((DefaultCategory) outcomeCategory, bookLine2));
        bookLine2.add(CategoryEnum.FLOW, bookLineCategory2);

        bookLineRepository.save(bookLine);
        bookLineRepository.save(bookLine2);

        LocalDate start = LocalDate.of(2023, 10, 21);
        LocalDate end = LOCAL_DATE;

        DatesDuration datesRequest = DatesDuration.builder()
                .startDate(start)
                .endDate(end)
                .build();
        AllOutcomesRequest request = new AllOutcomesRequest(BOOK_KEY, Collections.singletonList(EMAIL), datesRequest);
        assertThat(bookLineRepository.getAllLines(request).size()).isEqualTo(2);
    }

    @Test
    @DisplayName("가계부 내역을 추가 시, 카테고리 3개(내역, 자산, 내역 분류)을 선택하여 생성한다")
    void saveBookLine() {
        /* given */
        final BookLine bookLine = bookLineRepository.save(createBookLineWithWriter(book, 1000.0, bookUser));

        // 내역 카테고리
        final DefaultCategory flowCategory = DefaultCategory.builder()
                .name("수입")
                .build();
        categoryRepository.save(flowCategory);

        final BookLineCategory bookLineFlowCategory = bookLineCategoryRepository.save(createFlowCategory(flowCategory, bookLine));
        bookLine.add(CategoryEnum.FLOW, bookLineFlowCategory);

        // 자산 카테고리
        final DefaultCategory assetCategory = DefaultCategory.builder()
                .name("은행")
                .build();
        categoryRepository.save(assetCategory);

        final BookLineCategory bookLineAssetCategory = bookLineCategoryRepository.save(createLineCategory(assetCategory, bookLine));
        bookLine.add(CategoryEnum.ASSET, bookLineAssetCategory);

        // 내역 분류 카테고리
        final DefaultCategory flowLineCategory = DefaultCategory.builder()
                .name("급여")
                .build();
        categoryRepository.save(flowLineCategory);

        final BookLineCategory bookLineFlowLineCategory = bookLineCategoryRepository.save(createLineCategory(flowLineCategory, bookLine));
        bookLine.add(CategoryEnum.FLOW_LINE, bookLineFlowLineCategory);

        /* when */
        final Map<CategoryEnum, BookLineCategory> result = bookLine.getBookLineCategories();

        /* then */
        assertThat(result).hasSize(3)
                .hasEntrySatisfying(
                        CategoryEnum.FLOW,
                        bookLineCategory -> assertThat(bookLineCategory).isSameAs(bookLineFlowCategory)
                )
                .hasEntrySatisfying(
                        CategoryEnum.ASSET,
                        bookLineCategory -> assertThat(bookLineCategory).isSameAs(bookLineAssetCategory)
                )
                .hasEntrySatisfying(
                        CategoryEnum.FLOW_LINE,
                        bookLineCategory -> assertThat(bookLineCategory).isSameAs(bookLineFlowLineCategory)
                );
    }

    @Test
    @DisplayName("카테고리 별 분석을 조회하면, 카테고리의 이름과 해당 월의 내역 합계가 나온다 - 성공")
    void analyzeByCategory() {
        /* given */
        final BookLine bookLine = bookLineRepository.save(createBookLineWithWriter(book, 1000.0, bookUser));

        // 카테고리 생성
        final DefaultCategory category = DefaultCategory.builder()
                .name("급여")
                .build();
        categoryRepository.save(category);

        final DefaultCategory category2 = DefaultCategory.builder()
                .name("용돈")
                .build();
        categoryRepository.save(category2);

        // 가계부 내역과 급여 카테고리 매핑
        final BookLineCategory bookLineFlowLineCategory = bookLineCategoryRepository.save(createLineCategory(category, bookLine));
        bookLine.add(CategoryEnum.FLOW_LINE, bookLineFlowLineCategory);

        // 가계부 내역2와 급여 카테고리 매핑
        final BookLine bookLine2 = bookLineRepository.save(createBookLineWithWriter(book, 1000.0, bookUser));
        final BookLineCategory bookLineFlowLineCategory2 = bookLineCategoryRepository.save(createLineCategory(category2, bookLine2));
        bookLine2.add(CategoryEnum.FLOW_LINE, bookLineFlowLineCategory2);

        // 가계부 내역3과 용돈 카테고리 매핑
        final BookLine bookLine3 = bookLineRepository.save(createBookLineWithWriter(book, 1000.0, bookUser));
        final BookLineCategory bookLineFlowLineCategory3 = bookLineCategoryRepository.save(createLineCategory(category, bookLine3));
        bookLine3.add(CategoryEnum.FLOW_LINE, bookLineFlowLineCategory3);

        final DatesDuration datesDuration = DatesDuration.builder()
                .startDate(LOCAL_DATE)
                .endDate(LOCAL_DATE.plusDays(1))
                .build();

        /* when */
        final List<AnalyzeResponseByCategory> results = bookLineRepository.analyzeByCategory(
                Arrays.asList(category2, category), datesDuration, book.getBookKey()
        );

        /* then */
        final List<String> categoryName = Arrays.asList("급여", "용돈");
        final List<Double> analyzeResult = Arrays.asList(2000.0, 1000.0);

        for (final AnalyzeResponseByCategory result : results) {
            assertThat(result)
                    .extracting(AnalyzeResponseByCategory::getCategory)
                    .usingRecursiveComparison()
                    .isIn(categoryName);

            assertThat(result)
                    .extracting(AnalyzeResponseByCategory::getMoney)
                    .usingRecursiveComparison()
                    .isIn(analyzeResult);
        }
    }
}


