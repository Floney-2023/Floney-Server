package com.floney.floney.book;

import com.floney.floney.analyze.dto.response.AnalyzeResponseByCategory;
import com.floney.floney.book.domain.entity.*;
import com.floney.floney.book.dto.constant.CategoryEnum;
import com.floney.floney.book.dto.process.BookLineExpense;
import com.floney.floney.book.dto.process.DatesDuration;
import com.floney.floney.book.dto.process.TotalExpense;
import com.floney.floney.book.dto.request.AllOutcomesRequest;
import com.floney.floney.book.domain.entity.category.BookCategory;
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
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.*;

import static com.floney.floney.book.CategoryFixture.*;
import static com.floney.floney.fixture.BookFixture.*;
import static com.floney.floney.fixture.BookLineFixture.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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

    private Category incomeCategory;
    private Category outcomeCategory;

    private BookCategory childIncomeCategory;

    @BeforeEach
    void init() {
        user = userRepository.save(UserFixture.createUser());
        book = bookRepository.save(BookFixture.createBook());
        incomeCategory = categoryRepository.save(incomeBookCategory());
        outcomeCategory = categoryRepository.save(outComeBookCategory());
        childIncomeCategory = categoryRepository.save(createChildCategory(incomeCategory, book));
    }

    @Test
    @DisplayName("각 가계부 내역 지정된 날짜기간의 수입/지출의 총합을 조회한다")
    void income() {
        BookLine bookLine = bookLineRepository.save(createBookLine(book, 1000f));
        BookLine bookLine2 = bookLineRepository.save(createBookLine(book, 1000f));

        BookLineCategory category = bookLineCategoryRepository.save(createFlowCategory((DefaultCategory) incomeCategory, bookLine));
        bookLine.add(CategoryEnum.FLOW, category);

        BookLineCategory category2 = bookLineCategoryRepository.save(createFlowCategory((DefaultCategory) outcomeCategory, bookLine2));
        bookLine2.add(CategoryEnum.FLOW, category2);

        bookLineRepository.save(bookLine);
        bookLineRepository.save(bookLine2);

        LocalDate start = LocalDate.of(2023, 10, 21);
        LocalDate end = LOCAL_DATE;

        DatesDuration dates = DatesDuration.builder()
            .startDate(start)
            .endDate(end)
            .build();

        BookLineExpense income = BookLineExpense.builder()
            .money(1000f)
            .assetType("수입")
            .date(LOCAL_DATE)
            .build();

        BookLineExpense outcome = BookLineExpense.builder()
            .money(1000f)
            .assetType("지출")
            .date(LOCAL_DATE)
            .build();

        Assertions.assertThat(bookLineRepository.dayIncomeAndOutcome(BOOK_KEY, dates))
            .isEqualTo(Arrays.asList(income, outcome));

    }

    @Test
    @DisplayName("각 가계부 내역 지정된 달의 총수입/총지출을 조회한다")
    void all_expenses() {
        BookLine bookLine = bookLineRepository.save(createBookLine(book, 1000f));
        BookLine bookLine2 = bookLineRepository.save(createBookLine(book, 1000f));

        BookLineCategory category = bookLineCategoryRepository.save(createFlowCategory((DefaultCategory) incomeCategory, bookLine));
        bookLine.add(CategoryEnum.FLOW, category);

        BookLineCategory category2 = bookLineCategoryRepository.save(createFlowCategory((DefaultCategory) outcomeCategory, bookLine2));
        bookLine2.add(CategoryEnum.FLOW, category2);

        bookLineRepository.save(bookLine);
        bookLineRepository.save(bookLine2);

        LocalDate start = LocalDate.of(2023, 10, 1);
        LocalDate end = LOCAL_DATE;

        DatesDuration dates = DatesDuration.builder()
            .startDate(start)
            .endDate(end)
            .build();

        Map<String, Double> totals = new HashMap<>();
        totals.put("수입", 1000.0);
        totals.put("지출", 1000.0);

        Assertions.assertThat(bookLineRepository.totalExpenseByMonth(BOOK_KEY, dates))
            .isEqualTo(totals);

    }

    @Test
    @DisplayName("날짜별로 총수입/총지출을 조회한다")
    void day_expenses() {
        BookLine bookLine = bookLineRepository.save(createBookLine(book, 1000.0));
        BookLine bookLine2 = bookLineRepository.save(createBookLine(book, 1000.0));

        BookLineCategory category = bookLineCategoryRepository.save(createFlowCategory((DefaultCategory) incomeCategory, bookLine));
        bookLine.add(CategoryEnum.FLOW, category);

        BookLineCategory category2 = bookLineCategoryRepository.save(createFlowCategory((DefaultCategory) outcomeCategory, bookLine2));
        bookLine2.add(CategoryEnum.FLOW, category2);

        bookLineRepository.save(bookLine);
        bookLineRepository.save(bookLine2);

        LocalDate target = LOCAL_DATE;

        TotalExpense income = TotalExpense.builder()
            .money(1000f)
            .assetType("수입")
            .build();

        TotalExpense outcome = TotalExpense.builder()
            .money(1000f)
            .assetType("지출")
            .build();

        Assertions.assertThat(bookLineRepository.totalExpenseByDay(target, BOOK_KEY))
            .isEqualTo(Arrays.asList(income, outcome));
    }

    @Test
    @DisplayName("날짜 별로 가계부 내역과 연관된 모든 카테고리와 금액을 조회한다")
    void days_line() {
        BookUser bookUser = bookUserRepository.save(createBookUser(user, book));
        BookLine bookLine = bookLineRepository.save(createBookLineWith(bookUser, book, 1000f));
        BookLine bookLine2 = bookLineRepository.save(createBookLineWith(bookUser, book, 1000f));

        BookLineCategory incomeBookLineCategory = bookLineCategoryRepository.save(createFlowCategory((DefaultCategory) incomeCategory, bookLine));
        BookLineCategory childLineCategory = bookLineCategoryRepository.save(createChildLineCategory(childIncomeCategory, bookLine));
        bookLine.add(CategoryEnum.FLOW, incomeBookLineCategory);
        bookLine.add(CategoryEnum.FLOW_LINE, childLineCategory);

        BookLineCategory bookLineCategory2 = bookLineCategoryRepository.save(createFlowCategory((DefaultCategory) outcomeCategory, bookLine2));
        bookLine2.add(CategoryEnum.FLOW, bookLineCategory2);

        bookLineRepository.save(bookLine);
        bookLineRepository.save(bookLine2);

        LocalDate targetDate = LOCAL_DATE;
        Assertions.assertThat(bookLineRepository.allLinesByDay(targetDate, BOOK_KEY).size())
            .isEqualTo(3);

    }

    @Test
    @DisplayName("정산에 참여하는 유저가 사용한 내역만 기간 내에 조회")
    void all_dates_with_bookUsers() {
        BookUser bookUser = bookUserRepository.save(BookFixture.createBookUser(user, book));

        BookLine bookLine = bookLineRepository.save(createBookLineWithWriter(book, 1000L, bookUser));
        BookLine bookLine2 = bookLineRepository.save(createBookLineWithWriter(book, 1000L, bookUser));

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
        Assertions.assertThat(bookLineRepository.getAllLines(request).size())
            .isEqualTo(2);
    }

    @Test
    @DisplayName("가계부 내역을 추가시, 카테고리 3개(자산,분류,내용)을 선택하여 생성한다")
    void saveBookLine() {
        BookLine bookLine = bookLineRepository.save(createBookLine(book, 1000f));

        DefaultCategory flowCategory = DefaultCategory.builder()
            .name("수입")
            .build();

        Category savedFlowCategory = categoryRepository.save(flowCategory);
        BookLineCategory savedBookLineCategory = bookLineCategoryRepository.save(createFlowCategory((DefaultCategory) savedFlowCategory, bookLine));
        bookLine.add(CategoryEnum.FLOW, savedBookLineCategory);
       
        DefaultCategory assetCategory = DefaultCategory.builder()
            .name("은행")
            .build();
        Category savedAssetCategory = categoryRepository.save(assetCategory);
        BookLineCategory bookLineAssetCategory = bookLineCategoryRepository.save(createLineCategory((DefaultCategory) savedAssetCategory, bookLine));
        bookLine.add(CategoryEnum.ASSET,bookLineAssetCategory);

        DefaultCategory flowLineCategory = DefaultCategory.builder()
            .name("급여")
            .build();
        Category savedFlowLineCategory = categoryRepository.save(flowLineCategory);
        BookLineCategory bookLineFlowLineCategory = bookLineCategoryRepository.save(createLineCategory((DefaultCategory) savedFlowLineCategory, bookLine));
        bookLine.add(CategoryEnum.FLOW_LINE,bookLineFlowLineCategory);

        BookLine savedBookLine = bookLineRepository.save(bookLine);
        assertThat(savedBookLine.getBookLineCategories().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("카테고리 별 분석을 조회하면, 카테고리의 이름과 해당 월의 내역 합계가 나온다 - 성공")
    void analyzeByCategory() {
        BookLine bookLine = bookLineRepository.save(createBookLine(book, 1000));

        // 1. 카테고리 생성
        DefaultCategory category = DefaultCategory.builder()
            .name("급여")
            .build();
        DefaultCategory savedCategory = categoryRepository.save(category);

        DefaultCategory category2 = DefaultCategory.builder()
            .name("용돈")
            .build();
        DefaultCategory savedCategory2 = categoryRepository.save(category2);

        // 2. 가계부 내역과 급여 카테고리 매핑
        BookLineCategory bookLineFlowLineCategory = bookLineCategoryRepository.save(createLineCategory(savedCategory, bookLine));
        bookLine.add(CategoryEnum.FLOW_LINE,bookLineFlowLineCategory);
       bookLineRepository.save(bookLine);

       // 3. 가계부 내역2와 급여 카테고리 매핑
        BookLine bookLine2 = bookLineRepository.save(createBookLine(book, 1000));
        BookLineCategory bookLineFlowLineCategory2 = bookLineCategoryRepository.save(createLineCategory(savedCategory2, bookLine));
        bookLine2.add(CategoryEnum.FLOW_LINE,bookLineFlowLineCategory2);
        bookLineRepository.save(bookLine2);

        // 4. 가계부 내역3과 용돈 카테고리 매핑
        BookLine bookLine3 = bookLineRepository.save(createBookLine(book, 1000));
        BookLineCategory bookLineFlowLineCategory3 = bookLineCategoryRepository.save(createLineCategory(savedCategory, bookLine));
        bookLine2.add(CategoryEnum.FLOW_LINE,bookLineFlowLineCategory3);
        bookLineRepository.save(bookLine3);

        DatesDuration datesDuration = DatesDuration.builder()
                 .startDate(LOCAL_DATE)
                     .endDate(LOCAL_DATE.plusDays(1))
                         .build();

        List<AnalyzeResponseByCategory> responses = bookLineRepository.analyzeByCategory(Arrays.asList(category2,category),datesDuration,book.getBookKey());

        for (AnalyzeResponseByCategory response : responses) {
            assertThat(response.getCategory())
                .isIn("급여", "용돈");
            assertThat((response.getMoney())).isIn(2000.0, 1000.0);
        }
    }
}


