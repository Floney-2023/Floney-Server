package com.floney.floney.book.repository;

import com.floney.floney.analyze.dto.request.AnalyzeByCategoryRequest;
import com.floney.floney.analyze.dto.response.AnalyzeResponseByCategory;
import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.book.domain.category.entity.Category;
import com.floney.floney.book.domain.category.entity.Subcategory;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookLine;
import com.floney.floney.book.domain.entity.BookLineCategory;
import com.floney.floney.book.domain.entity.BookUser;
import com.floney.floney.book.dto.process.BookLineExpense;
import com.floney.floney.book.dto.request.AllOutcomesRequest;
import com.floney.floney.book.repository.category.CategoryRepository;
import com.floney.floney.book.repository.category.SubcategoryRepository;
import com.floney.floney.common.domain.vo.DateDuration;
import com.floney.floney.config.QueryDslTest;
import com.floney.floney.fixture.BookFixture;
import com.floney.floney.fixture.BookLineFixture;
import com.floney.floney.fixture.UserFixture;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.UserRepository;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.floney.floney.book.domain.category.CategoryType.*;
import static com.floney.floney.fixture.BookLineFixture.DEFAULT_MONEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@QueryDslTest
@DisplayName("단위 테스트: BookLineCustomRepository")
class BookLineCustomRepositoryTest {

    @Autowired
    private BookLineRepository bookLineRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookUserRepository bookUserRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SubcategoryRepository subcategoryRepository;

    @Nested
    @DisplayName("findAllByDurationOrderByDateDesc()를 실행할 때")
    class Describe_FindAllByDurationOrderByDateDesc {

        final Category lineCategory = categoryRepository.findByType(INCOME).orElseThrow();
        final Category assetCategory = categoryRepository.findByType(ASSET).orElseThrow();

        @Nested
        @DisplayName("특정 book에 bookLine이 존재하는 경우")
        class Context_With_BookLine {

            final String bookKey = "AAAAAA";
            final DateDuration dateDuration = new DateDuration(LocalDate.now(), LocalDate.now().plusYears(1));

            List<BookLine> bookLines;

            @BeforeEach
            void init() {
                final Book book = bookRepository.save(BookFixture.createBookWith(bookKey));
                final User user = userRepository.save(UserFixture.emailUser());
                final BookUser bookUser = bookUserRepository.save(BookUser.of(user, book));

                final Subcategory lineSubcategory = subcategoryRepository.save(Subcategory.of(lineCategory, book, "급"));
                final Subcategory assetSubcategory = subcategoryRepository.save(Subcategory.of(assetCategory, book, "현금"));

                bookLines = List.of(
                    BookLineFixture.createWithDate(
                        book, bookUser, categories(lineCategory, lineSubcategory, assetSubcategory), LocalDate.now()
                    ),
                    BookLineFixture.createWithDate(
                        book, bookUser, categories(lineCategory, lineSubcategory, assetSubcategory), LocalDate.now().plusDays(5)
                    ),
                    BookLineFixture.createWithDate(
                        book, bookUser, categories(lineCategory, lineSubcategory, assetSubcategory), LocalDate.now().plusDays(1)
                    )
                );
                bookLineRepository.saveAll(bookLines);
            }

            @Test
            @DisplayName("lineDate로 내림차순 정렬된 bookLine 목록을 반환한다.")
            void it_returns_sortedBookLines() {
                assertThat(bookLineRepository.findAllByDurationOrderByDateDesc(bookKey, dateDuration))
                    .isSortedAccordingTo(Comparator.comparing(BookLine::getLineDate, Comparator.reverseOrder()));
            }
        }

        @Nested
        @DisplayName("기간에 포함된 bookLine이 존재하지 않는 경우")
        class Context_With_OutdatedBookLine {

            final String bookKey = "AAAAAA";
            final DateDuration dateDuration = new DateDuration(LocalDate.now(), LocalDate.now().plusDays(1));

            List<BookLine> bookLines;

            @BeforeEach
            void init() {
                final Book book = bookRepository.save(BookFixture.createBookWith(bookKey));
                final User user = userRepository.save(UserFixture.emailUser());
                final BookUser bookUser = bookUserRepository.save(BookUser.of(user, book));

                final Subcategory lineSubcategory = subcategoryRepository.save(Subcategory.of(lineCategory, book, "급여"));
                final Subcategory assetSubcategory = subcategoryRepository.save(Subcategory.of(assetCategory, book, "현금"));

                bookLines = List.of(
                    BookLineFixture.createWithDate(
                        book, bookUser, categories(lineCategory, lineSubcategory, assetSubcategory), LocalDate.now().plusYears(1)
                    ),
                    BookLineFixture.createWithDate(
                        book, bookUser, categories(lineCategory, lineSubcategory, assetSubcategory), LocalDate.now().plusDays(5)
                    )
                );
                bookLineRepository.saveAll(bookLines);
            }

            @Test
            @DisplayName("빈 목록을 반환한다.")
            void it_returns_empty() {
                assertThat(bookLineRepository.findAllByDurationOrderByDateDesc(bookKey, dateDuration))
                    .isEmpty();
            }
        }
    }

    @Nested
    @DisplayName("allLinesByDay()를 실행할 때")
    class Describe_AllLinesByDay {

        final Category lineCategory = categoryRepository.findByType(INCOME).orElseThrow();
        final Category assetCategory = categoryRepository.findByType(ASSET).orElseThrow();

        @Nested
        @DisplayName("특정 날짜에 bookLine이 존재하는 경우")
        class Context_With_BookLineByDay {

            final String bookKey = "AAAAAA";
            final LocalDate date = LocalDate.now();

            @BeforeEach
            void init() {
                final Book book = bookRepository.save(BookFixture.createBookWith(bookKey));
                final User user = userRepository.save(UserFixture.emailUser());
                final BookUser bookUser = bookUserRepository.save(BookUser.of(user, book));

                final Subcategory lineSubcategory = subcategoryRepository.save(Subcategory.of(lineCategory, book, "급여"));
                final Subcategory assetSubcategory = subcategoryRepository.save(Subcategory.of(assetCategory, book, "현금"));

                final List<BookLine> bookLines = List.of(
                    BookLineFixture.createWithDate(
                        book, bookUser, categories(lineCategory, lineSubcategory, assetSubcategory), date
                    ),
                    BookLineFixture.createWithDate(
                        book, bookUser, categories(lineCategory, lineSubcategory, assetSubcategory), date
                    )
                );
                bookLineRepository.saveAll(bookLines);
            }

            @Test
            @DisplayName("BookLineWithWriterView 목록을 반환한다.")
            void it_returns_bookLineWithWriterViews() {
                assertThat(bookLineRepository.allLinesByDay(date, bookKey))
                    .hasSize(2);
            }
        }

        @Nested
        @DisplayName("다른 날짜에 bookLine이 존재하는 경우")
        class Context_With_BookLineByAnotherDay {

            final String bookKey = "AAAAAA";
            final LocalDate date = LocalDate.now();

            @BeforeEach
            void init() {
                final Book book = bookRepository.save(BookFixture.createBookWith(bookKey));
                final User user = userRepository.save(UserFixture.emailUser());
                final BookUser bookUser = bookUserRepository.save(BookUser.of(user, book));

                final Subcategory lineSubcategory = subcategoryRepository.save(Subcategory.of(lineCategory, book, "급여"));
                final Subcategory assetSubcategory = subcategoryRepository.save(Subcategory.of(assetCategory, book, "현금"));

                final List<BookLine> bookLines = List.of(
                    BookLineFixture.createWithDate(
                        book, bookUser, categories(lineCategory, lineSubcategory, assetSubcategory), date.plusDays(1)
                    )
                );
                bookLineRepository.saveAll(bookLines);
            }

            @Test
            @DisplayName("빈 목록을 반환한다.")
            void it_returns_empty() {
                assertThat(bookLineRepository.allLinesByDay(date, bookKey))
                    .isEmpty();
            }
        }
    }

    @Nested
    @DisplayName("totalMoneyByDateAndCategoryType()를 실행할 때")
    class Describe_TotalMoneyByDateAndCategoryType {

        final Category incomeCategory = categoryRepository.findByType(INCOME).orElseThrow();
        final Category assetCategory = categoryRepository.findByType(ASSET).orElseThrow();

        @Nested
        @DisplayName("특정 lineCategory를 가진 bookLine들이 해당 날짜에 존재하는 경우")
        class Context_With_BookLineByDay {

            final String bookKey = "AAAAAA";
            final LocalDate date = LocalDate.now();

            double totalMoney;

            @BeforeEach
            void init() {
                final Book book = bookRepository.save(BookFixture.createBookWith(bookKey));
                final User user = userRepository.save(UserFixture.emailUser());
                final BookUser bookUser = bookUserRepository.save(BookUser.of(user, book));

                final Subcategory incomeSubcategory = subcategoryRepository.save(Subcategory.of(incomeCategory, book, "급여"));
                final Subcategory assetSubcategory = subcategoryRepository.save(Subcategory.of(assetCategory, book, "현금"));

                final List<BookLine> bookLines = List.of(
                    BookLineFixture.createWithDate(
                        book, bookUser, categories(incomeCategory, incomeSubcategory, assetSubcategory), date
                    ),
                    BookLineFixture.createWithDate(
                        book, bookUser, categories(incomeCategory, incomeSubcategory, assetSubcategory), date
                    )
                );
                bookLineRepository.saveAll(bookLines);

                totalMoney = bookLines.stream().mapToDouble(BookLine::getMoney).sum();
            }

            @Test
            @DisplayName("TotalExpense를 반환한다.")
            void it_returns_totalExpense() {
                final CategoryType categoryType = INCOME;

                assertThat(bookLineRepository.totalMoneyByDateAndCategoryType(bookKey, date, categoryType))
                    .isNotNull()
                    .hasFieldOrPropertyWithValue("categoryType", categoryType)
                    .hasFieldOrPropertyWithValue("money", totalMoney);
            }
        }

        @Nested
        @DisplayName("해당 날짜의 bookLine들이 다른 lineCategory를 가지는 경우")
        class Context_With_BookLineWithDifferentCategoryByDay {

            final String bookKey = "AAAAAA";
            final LocalDate date = LocalDate.now();

            @BeforeEach
            void init() {
                final Book book = bookRepository.save(BookFixture.createBookWith(bookKey));
                final User user = userRepository.save(UserFixture.emailUser());
                final BookUser bookUser = bookUserRepository.save(BookUser.of(user, book));

                final Subcategory lineSubcategory = subcategoryRepository.save(Subcategory.of(incomeCategory, book, "급여"));
                final Subcategory assetSubcategory = subcategoryRepository.save(Subcategory.of(assetCategory, book, "현금"));

                final List<BookLine> bookLines = List.of(
                    BookLineFixture.createWithDate(
                        book, bookUser, categories(incomeCategory, lineSubcategory, assetSubcategory), date
                    ),
                    BookLineFixture.createWithDate(
                        book, bookUser, categories(incomeCategory, lineSubcategory, assetSubcategory), date
                    )
                );
                bookLineRepository.saveAll(bookLines);
            }

            @Test
            @DisplayName("money가 0.0인 TotalExpense를 반환한다.")
            void it_returns_totalExpense() {
                final CategoryType categoryType = OUTCOME;

                assertThat(bookLineRepository.totalMoneyByDateAndCategoryType(bookKey, date, categoryType))
                    .hasFieldOrPropertyWithValue("categoryType", categoryType)
                    .hasFieldOrPropertyWithValue("money", 0.0);
            }
        }
    }

    @Nested
    @DisplayName("findIncomeAndOutcomeByDurationPerDay()를 실행할 때")
    class Describe_FindIncomeAndOutcomeByDurationPerDay {

        final Category incomeCategory = categoryRepository.findByType(INCOME).orElseThrow();
        final Category outcomeCategory = categoryRepository.findByType(OUTCOME).orElseThrow();
        final Category assetCategory = categoryRepository.findByType(ASSET).orElseThrow();
        final Category transferCategory = categoryRepository.findByType(TRANSFER).orElseThrow();

        @Nested
        @DisplayName("특정 기간에 지출 또는 수입인 bookLine이 존재하는 경우")
        class Context_With_IncomeOrOutcomeBookLines {

            final String bookKey = "AAAAAA";
            final DateDuration dateDuration = new DateDuration(LocalDate.now(), LocalDate.now().plusYears(1));

            @BeforeEach
            void init() {
                final Book book = bookRepository.save(BookFixture.createBookWith(bookKey));
                final User user = userRepository.save(UserFixture.emailUser());
                final BookUser bookUser = bookUserRepository.save(BookUser.of(user, book));

                final Subcategory incomeSubcategory = subcategoryRepository.save(Subcategory.of(incomeCategory, book, "급여"));
                final Subcategory outcomeSubcategory = subcategoryRepository.save(Subcategory.of(incomeCategory, book, "식비"));
                final Subcategory assetSubcategory = subcategoryRepository.save(Subcategory.of(assetCategory, book, "현금"));

                final List<BookLine> bookLines = List.of(
                    BookLineFixture.createWithDate(
                        book, bookUser, categories(incomeCategory, incomeSubcategory, assetSubcategory), LocalDate.now()
                    ),
                    BookLineFixture.createWithDate(
                        book, bookUser, categories(incomeCategory, incomeSubcategory, assetSubcategory), LocalDate.now()
                    ),
                    BookLineFixture.createWithDate(
                        book, bookUser, categories(outcomeCategory, outcomeSubcategory, assetSubcategory), LocalDate.now()
                    ),
                    BookLineFixture.createWithDate(
                        book, bookUser, categories(incomeCategory, incomeSubcategory, assetSubcategory), LocalDate.now().plusDays(1)
                    ),
                    BookLineFixture.createWithDate(
                        book, bookUser, categories(outcomeCategory, outcomeSubcategory, assetSubcategory), LocalDate.now().plusDays(1)
                    )
                );
                bookLineRepository.saveAll(bookLines);
            }

            @Test
            @DisplayName("BookLineExpense 목록을 반환한다.")
            void it_returns_bookLineExpenses() {
                final List<BookLineExpense> result = List.of(
                    new BookLineExpense(LocalDate.now(), DEFAULT_MONEY * 2, INCOME),
                    new BookLineExpense(LocalDate.now(), DEFAULT_MONEY, OUTCOME),
                    new BookLineExpense(LocalDate.now().plusDays(1), DEFAULT_MONEY, INCOME),
                    new BookLineExpense(LocalDate.now().plusDays(1), DEFAULT_MONEY, OUTCOME)
                );

                assertThat(bookLineRepository.findIncomeAndOutcomeByDurationPerDay(bookKey, dateDuration))
                    .usingRecursiveComparison()
                    .isEqualTo(result);
            }
        }

        @Nested
        @DisplayName("특정 기간에 지출 또는 수입인 bookLine이 존재하지 않는 경우")
        class Context_With_NotIncomeOrOutcomeBookLines {

            final String bookKey = "AAAAAA";
            final DateDuration dateDuration = new DateDuration(LocalDate.now(), LocalDate.now().plusYears(1));

            @BeforeEach
            void init() {
                final Book book = bookRepository.save(BookFixture.createBookWith(bookKey));
                final User user = userRepository.save(UserFixture.emailUser());
                final BookUser bookUser = bookUserRepository.save(BookUser.of(user, book));

                final Subcategory transferSubcategory = subcategoryRepository.save(Subcategory.of(transferCategory, book, "급여"));
                final Subcategory assetSubcategory = subcategoryRepository.save(Subcategory.of(assetCategory, book, "현금"));

                final List<BookLine> bookLines = List.of(
                    BookLineFixture.createWithDate(
                        book, bookUser, categories(transferCategory, transferSubcategory, assetSubcategory), LocalDate.now()
                    ),
                    BookLineFixture.createWithDate(
                        book, bookUser, categories(transferCategory, transferSubcategory, assetSubcategory), LocalDate.now()
                    )
                );
                bookLineRepository.saveAll(bookLines);
            }

            @Test
            @DisplayName("빈 목록을 반환한다.")
            void it_returns_empty() {
                assertThat(bookLineRepository.findIncomeAndOutcomeByDurationPerDay(bookKey, dateDuration))
                    .isEmpty();
            }
        }

        @Nested
        @DisplayName("지출 또는 수입인 bookLine이 다른 기간에만 존재하는 경우")
        class Context_With_IncomeOrOutcomeBookLinesAtOtherDuration {

            final String bookKey = "AAAAAA";
            final DateDuration dateDuration = new DateDuration(LocalDate.now(), LocalDate.now());

            @BeforeEach
            void init() {
                final Book book = bookRepository.save(BookFixture.createBookWith(bookKey));
                final User user = userRepository.save(UserFixture.emailUser());
                final BookUser bookUser = bookUserRepository.save(BookUser.of(user, book));

                final Subcategory incomeSubcategory = subcategoryRepository.save(Subcategory.of(incomeCategory, book, "급여"));
                final Subcategory outcomeSubcategory = subcategoryRepository.save(Subcategory.of(outcomeCategory, book, "식비"));
                final Subcategory assetSubcategory = subcategoryRepository.save(Subcategory.of(assetCategory, book, "현금"));

                final List<BookLine> bookLines = List.of(
                    BookLineFixture.createWithDate(
                        book, bookUser, categories(incomeCategory, incomeSubcategory, assetSubcategory), LocalDate.now().plusDays(1)
                    ),
                    BookLineFixture.createWithDate(
                        book, bookUser, categories(outcomeCategory, outcomeSubcategory, assetSubcategory), LocalDate.now().plusDays(1)
                    )
                );
                bookLineRepository.saveAll(bookLines);
            }

            @Test
            @DisplayName("빈 목록을 반환한다.")
            void it_returns_empty() {
                assertThat(bookLineRepository.findIncomeAndOutcomeByDurationPerDay(bookKey, dateDuration))
                    .isEmpty();
            }
        }
    }

    @Nested
    @DisplayName("findAllOutcomes()를 실행할 때")
    class Describe_FindAllOutcomes {

        final Category incomeCategory = categoryRepository.findByType(INCOME).orElseThrow();
        final Category outcomeCategory = categoryRepository.findByType(OUTCOME).orElseThrow();
        final Category assetCategory = categoryRepository.findByType(ASSET).orElseThrow();
        final Category transferCategory = categoryRepository.findByType(TRANSFER).orElseThrow();

        @Nested
        @DisplayName("특정 기간에 유저들이 작성한 지출인 bookLine이 존재하는 경우")
        class Context_With_OutcomeBookLine {

            final String bookKey = "AAAAAA";
            final List<String> userEmails = List.of("test1@email.com", "test2@email.com");
            final DateDuration dateDuration = new DateDuration(LocalDate.now(), LocalDate.now().plusYears(1));

            @BeforeEach
            void init() {
                final Book book = bookRepository.save(BookFixture.createBookWith(bookKey));

                final ArrayList<BookUser> bookUsers = new ArrayList<>();
                for (final String email : userEmails) {
                    final User user = userRepository.save(UserFixture.emailUserWithEmail(email));
                    bookUsers.add(bookUserRepository.save(BookUser.of(user, book)));
                }

                final Subcategory outcomeSubcategory = subcategoryRepository.save(Subcategory.of(outcomeCategory, book, "식비"));
                final Subcategory assetSubcategory = subcategoryRepository.save(Subcategory.of(assetCategory, book, "현금"));

                final List<BookLine> bookLines = bookUsers.stream()
                    .map(bookUser -> BookLineFixture.createWithDate(
                        book, bookUser, categories(outcomeCategory, outcomeSubcategory, assetSubcategory), LocalDate.now().plusDays(1)
                    ))
                    .toList();

                bookLineRepository.saveAll(bookLines);
            }

            @Test
            @DisplayName("BookLineWithWriterView 목록을 반환한다.")
            void it_returns_bookLineWithWriterViews() {
                assertThat(bookLineRepository.findAllOutcomes(new AllOutcomesRequest(bookKey, userEmails, dateDuration)))
                    .hasSize(2)
                    .extracting("money")
                    .allSatisfy(money -> assertThat(money).isEqualTo(DEFAULT_MONEY));
            }
        }

        @Nested
        @DisplayName("특정 기간에 다른 유저들의 bookLine만 존재하는 경우")
        class Context_With_OutcomeBookLineOfOtherUsers {

            final String bookKey = "AAAAAA";
            final List<String> userEmails = List.of("test@email.com");
            final DateDuration dateDuration = new DateDuration(LocalDate.now(), LocalDate.now().plusYears(1));

            @BeforeEach
            void init() {
                final Book book = bookRepository.save(BookFixture.createBookWith(bookKey));

                final User user = userRepository.save(UserFixture.emailUserWithEmail("other@email.com"));
                final BookUser bookUser = bookUserRepository.save(BookUser.of(user, book));

                final Subcategory outcomeSubcategory = subcategoryRepository.save(Subcategory.of(outcomeCategory, book, "식비"));
                final Subcategory assetSubcategory = subcategoryRepository.save(Subcategory.of(assetCategory, book, "현금"));

                final List<BookLine> bookLines = List.of(
                    BookLineFixture.createWithDate(
                        book, bookUser, categories(outcomeCategory, outcomeSubcategory, assetSubcategory), LocalDate.now().plusDays(1)
                    )
                );

                bookLineRepository.saveAll(bookLines);
            }

            @Test
            @DisplayName("빈 목록을 반환한다.")
            void it_returns_empty() {
                assertThat(bookLineRepository.findAllOutcomes(new AllOutcomesRequest(bookKey, userEmails, dateDuration)))
                    .isEmpty();
            }
        }

        @Nested
        @DisplayName("특정 기간에 bookLine이 존재하지 않는 경우")
        class Context_With_NoBookLine {

            final String bookKey = "AAAAAA";
            final List<String> userEmails = List.of("test@email.com");
            final DateDuration dateDuration = new DateDuration(LocalDate.now(), LocalDate.now());

            @BeforeEach
            void init() {
                final Book book = bookRepository.save(BookFixture.createBookWith(bookKey));

                final ArrayList<BookUser> bookUsers = new ArrayList<>();
                for (final String email : userEmails) {
                    final User user = userRepository.save(UserFixture.emailUserWithEmail(email));
                    bookUsers.add(bookUserRepository.save(BookUser.of(user, book)));
                }

                final Subcategory outcomeSubcategory = subcategoryRepository.save(Subcategory.of(outcomeCategory, book, "식비"));
                final Subcategory assetSubcategory = subcategoryRepository.save(Subcategory.of(assetCategory, book, "현금"));

                final List<BookLine> bookLines = bookUsers.stream()
                    .map(bookUser -> BookLineFixture.createWithDate(
                        book, bookUser, categories(outcomeCategory, outcomeSubcategory, assetSubcategory), LocalDate.now().plusDays(1)
                    ))
                    .toList();

                bookLineRepository.saveAll(bookLines);
            }

            @Test
            @DisplayName("빈 목록을 반환한다.")
            void it_returns_empty() {
                assertThat(bookLineRepository.findAllOutcomes(new AllOutcomesRequest(bookKey, userEmails, dateDuration)))
                    .isEmpty();
            }
        }
    }

    @Nested
    @DisplayName("totalExpenseForBeforeMonth()를 실행할 때")
    class Describe_TotalExpenseForBeforeMonth {

        final Category incomeCategory = categoryRepository.findByType(INCOME).orElseThrow();
        final Category outcomeCategory = categoryRepository.findByType(OUTCOME).orElseThrow();
        final Category assetCategory = categoryRepository.findByType(ASSET).orElseThrow();

        @Nested
        @DisplayName("이전 달에 지출 bookLine이 존재하는 경우")
        class Context_With_OutcomeBookLinesBeforeMonth {

            final String bookKey = "AAAAAA";
            final LocalDate date = LocalDate.now();

            @BeforeEach
            void init() {
                final Book book = bookRepository.save(BookFixture.createBookWith(bookKey));
                final User user = userRepository.save(UserFixture.emailUser());
                final BookUser bookUser = bookUserRepository.save(BookUser.of(user, book));

                final Subcategory outcomeSubcategory = subcategoryRepository.save(Subcategory.of(outcomeCategory, book, "식비"));
                final Subcategory assetSubcategory = subcategoryRepository.save(Subcategory.of(assetCategory, book, "현금"));

                final List<BookLine> bookLines = List.of(
                    BookLineFixture.createWithDate(
                        book, bookUser, categories(outcomeCategory, outcomeSubcategory, assetSubcategory), date.minusMonths(1)
                    ),
                    BookLineFixture.createWithDate(
                        book, bookUser, categories(outcomeCategory, outcomeSubcategory, assetSubcategory), date.minusMonths(1)
                    )
                );
                bookLineRepository.saveAll(bookLines);
            }

            @Test
            @DisplayName("이전 달의 지출 총합을 반환한다.")
            void it_returns_double() {
                final AnalyzeByCategoryRequest request = new AnalyzeByCategoryRequest(bookKey, OUTCOME.getMeaning(), date.toString());

                assertThat(bookLineRepository.totalExpenseForBeforeMonth(request))
                    .isCloseTo(DEFAULT_MONEY * 2, Percentage.withPercentage(99.9));
            }
        }

        @Nested
        @DisplayName("다른 달에 지출 bookLine이 존재하는 경우")
        class Context_With_NoOutcomeBookLinesBeforeMonth {

            final String bookKey = "AAAAAA";
            final LocalDate date = LocalDate.now();

            @BeforeEach
            void init() {
                final Book book = bookRepository.save(BookFixture.createBookWith(bookKey));
                final User user = userRepository.save(UserFixture.emailUser());
                final BookUser bookUser = bookUserRepository.save(BookUser.of(user, book));

                final Subcategory outcomeSubcategory = subcategoryRepository.save(Subcategory.of(outcomeCategory, book, "식비"));
                final Subcategory assetSubcategory = subcategoryRepository.save(Subcategory.of(assetCategory, book, "현금"));

                final List<BookLine> bookLines = List.of(
                    BookLineFixture.createWithDate(
                        book, bookUser, categories(outcomeCategory, outcomeSubcategory, assetSubcategory), date
                    )
                );
                bookLineRepository.saveAll(bookLines);
            }

            @Test
            @DisplayName("0.0을 반환한다.")
            void it_returns_double() {
                final AnalyzeByCategoryRequest request = new AnalyzeByCategoryRequest(bookKey, OUTCOME.getMeaning(), date.toString());

                assertThat(bookLineRepository.totalExpenseForBeforeMonth(request))
                    .isCloseTo(0.0, Percentage.withPercentage(99.9));
            }
        }

        @Nested
        @DisplayName("이전 달에 지출이 아닌 bookLine이 존재하는 경우")
        class Context_With_BookLinesBeforeMonthNoOutcome {

            final String bookKey = "AAAAAA";
            final LocalDate date = LocalDate.now();

            @BeforeEach
            void init() {
                final Book book = bookRepository.save(BookFixture.createBookWith(bookKey));
                final User user = userRepository.save(UserFixture.emailUser());
                final BookUser bookUser = bookUserRepository.save(BookUser.of(user, book));

                final Subcategory incomeSubcategory = subcategoryRepository.save(Subcategory.of(incomeCategory, book, "급여"));
                final Subcategory assetSubcategory = subcategoryRepository.save(Subcategory.of(assetCategory, book, "현금"));

                final List<BookLine> bookLines = List.of(
                    BookLineFixture.createWithDate(
                        book, bookUser, categories(incomeCategory, incomeSubcategory, assetSubcategory), date.minusMonths(1)
                    )
                );
                bookLineRepository.saveAll(bookLines);
            }

            @Test
            @DisplayName("0.0을 반환한다.")
            void it_returns_double() {
                final AnalyzeByCategoryRequest request = new AnalyzeByCategoryRequest(bookKey, OUTCOME.getMeaning(), date.toString());

                assertThat(bookLineRepository.totalExpenseForBeforeMonth(request))
                    .isCloseTo(0.0, Percentage.withPercentage(99.9));
            }
        }
    }

    @Nested
    @DisplayName("analyzeByLineSubcategory()를 실행할 때")
    class Describe_AnalyzeByLineSubcategory {

        final Category incomeCategory = categoryRepository.findByType(INCOME).orElseThrow();
        final Category assetCategory = categoryRepository.findByType(ASSET).orElseThrow();

        @Nested
        @DisplayName("기간 내 lineSubcategory 목록에 포함되는 bookLine이 존재하는 경우")
        class Context_With_BookLineHasLineSubCategory {

            List<Subcategory> lineSubcategories;
            final String bookKey = "AAAAAA";
            final DateDuration dateDuration = new DateDuration(LocalDate.now(), LocalDate.now());

            @BeforeEach
            void init() {
                final Book book = bookRepository.save(BookFixture.createBookWith(bookKey));
                final User user = userRepository.save(UserFixture.emailUser());
                final BookUser bookUser = bookUserRepository.save(BookUser.of(user, book));

                final Subcategory assetSubcategory = subcategoryRepository.save(Subcategory.of(assetCategory, book, "현금"));
                lineSubcategories = List.of(
                    subcategoryRepository.save(Subcategory.of(incomeCategory, book, "급여")),
                    subcategoryRepository.save(Subcategory.of(incomeCategory, book, "용돈")),
                    subcategoryRepository.save(Subcategory.of(incomeCategory, book, "로또"))
                );

                for (final Subcategory lineSubcategory : lineSubcategories) {
                    bookLineRepository.save(
                        BookLineFixture.createWithDate(book, bookUser, categories(incomeCategory, lineSubcategory, assetSubcategory), LocalDate.now())
                    );
                }
            }

            @Test
            @DisplayName("AnalyzeResponseByCategory 목록을 반환한다.")
            void it_returns_analyzeResponseByCategories() {
                final List<AnalyzeResponseByCategory> result = List.of(
                    new AnalyzeResponseByCategory("급여", DEFAULT_MONEY),
                    new AnalyzeResponseByCategory("로또", DEFAULT_MONEY),
                    new AnalyzeResponseByCategory("용돈", DEFAULT_MONEY)
                );

                assertThat(bookLineRepository.analyzeByLineSubcategory(lineSubcategories, dateDuration, bookKey))
                    .usingRecursiveComparison()
                    .isEqualTo(result);
            }
        }

        @Nested
        @DisplayName("기간 내 lineSubcategory 목록에 포함되지 않는 bookLine이 존재하는 경우")
        class Context_With_BookLineHasOtherLineSubCategory {

            List<Subcategory> lineSubcategories;
            final String bookKey = "AAAAAA";
            final DateDuration dateDuration = new DateDuration(LocalDate.now(), LocalDate.now());

            @BeforeEach
            void init() {
                final Book book = bookRepository.save(BookFixture.createBookWith(bookKey));
                final User user = userRepository.save(UserFixture.emailUser());
                final BookUser bookUser = bookUserRepository.save(BookUser.of(user, book));

                lineSubcategories = List.of(
                    subcategoryRepository.save(Subcategory.of(incomeCategory, book, "급여"))
                );
                final Subcategory otherSubcategory = subcategoryRepository.save(Subcategory.of(incomeCategory, book, "로또"));
                final Subcategory assetSubcategory = subcategoryRepository.save(Subcategory.of(assetCategory, book, "현금"));

                bookLineRepository.save(
                    BookLineFixture.createWithDate(book, bookUser, categories(incomeCategory, otherSubcategory, assetSubcategory), LocalDate.now())
                );
            }

            @Test
            @DisplayName("빈 목록을 반환한다.")
            void it_returns_empty() {
                assertThat(bookLineRepository.analyzeByLineSubcategory(lineSubcategories, dateDuration, bookKey))
                    .isEmpty();
            }
        }

        @Nested
        @DisplayName("lineSubcategory가 아닌 다른 subcategory로 요청한 경우")
        class Context_With_NotLineSubCategory {

            List<Subcategory> lineSubcategories;
            final String bookKey = "AAAAAA";
            final DateDuration dateDuration = new DateDuration(LocalDate.now(), LocalDate.now());

            @BeforeEach
            void init() {
                final Book book = bookRepository.save(BookFixture.createBookWith(bookKey));
                final User user = userRepository.save(UserFixture.emailUser());
                final BookUser bookUser = bookUserRepository.save(BookUser.of(user, book));

                final Subcategory assetSubcategory = subcategoryRepository.save(Subcategory.of(assetCategory, book, "현금"));
                lineSubcategories = List.of(assetSubcategory);

                final Subcategory lineSubcategory = subcategoryRepository.save(Subcategory.of(incomeCategory, book, "급여"));
                bookLineRepository.save(
                    BookLineFixture.createWithDate(book, bookUser, categories(incomeCategory, lineSubcategory, assetSubcategory), LocalDate.now())
                );
            }

            @Test
            @DisplayName("예외를 반환한다.")
            void it_returns_exception() {
                assertThatThrownBy(() -> bookLineRepository.analyzeByLineSubcategory(lineSubcategories, dateDuration, bookKey))
                    .isInstanceOf(RuntimeException.class);
            }
        }
    }

    @Nested
    @DisplayName("totalOutcomeMoneyForBudget()를 실행할 때")
    class Describe_TotalOutcomeMoneyForBudget {

        final Category incomeCategory = categoryRepository.findByType(INCOME).orElseThrow();
        final Category outcomeCategory = categoryRepository.findByType(OUTCOME).orElseThrow();
        final Category assetCategory = categoryRepository.findByType(ASSET).orElseThrow();

        @Nested
        @DisplayName("특정 기간에 지출 bookLine이 존재하는 경우")
        class Context_With_OutcomeBookLine {

            final Book book = BookFixture.createBook();
            final DateDuration dateDuration = new DateDuration(LocalDate.now(), LocalDate.now());

            @BeforeEach
            void init() {
                bookRepository.save(book);
                final User user = userRepository.save(UserFixture.emailUser());
                final BookUser bookUser = bookUserRepository.save(BookUser.of(user, book));

                final Subcategory outcomeSubcategory = subcategoryRepository.save(Subcategory.of(outcomeCategory, book, "식비"));
                final Subcategory assetSubcategory = subcategoryRepository.save(Subcategory.of(assetCategory, book, "현금"));

                final List<BookLine> bookLines = List.of(
                    BookLineFixture.createWithDate(
                        book, bookUser, categories(outcomeCategory, outcomeSubcategory, assetSubcategory), LocalDate.now()
                    ),
                    BookLineFixture.createWithDate(
                        book, bookUser, categories(outcomeCategory, outcomeSubcategory, assetSubcategory), LocalDate.now()
                    )
                );
                bookLineRepository.saveAll(bookLines);
            }

            @Test
            @DisplayName("지출 총합을 반환한다.")
            void it_returns_double() {
                assertThat(bookLineRepository.totalOutcomeMoneyForBudget(book, dateDuration))
                    .isCloseTo(DEFAULT_MONEY * 2, Percentage.withPercentage(99.9));
            }
        }

        @Nested
        @DisplayName("bookLine의 exceptStatus가 true인 경우")
        class Context_With_BookLineWithTrueExceptStatus {

            final Book book = BookFixture.createBook();
            final DateDuration dateDuration = new DateDuration(LocalDate.now(), LocalDate.now());

            @BeforeEach
            void init() {
                bookRepository.save(book);
                final User user = userRepository.save(UserFixture.emailUser());
                final BookUser bookUser = bookUserRepository.save(BookUser.of(user, book));

                final Subcategory outcomeSubcategory = subcategoryRepository.save(Subcategory.of(outcomeCategory, book, "식비"));
                final Subcategory assetSubcategory = subcategoryRepository.save(Subcategory.of(assetCategory, book, "현금"));

                final List<BookLine> bookLines = List.of(
                    BookLineFixture.createWithDateNotBudget(
                        book, bookUser, categories(outcomeCategory, outcomeSubcategory, assetSubcategory), LocalDate.now()
                    )
                );
                bookLineRepository.saveAll(bookLines);
            }

            @Test
            @DisplayName("0.0을 반환한다.")
            void it_returns_double() {
                assertThat(bookLineRepository.totalOutcomeMoneyForBudget(book, dateDuration))
                    .isCloseTo(0.0, Percentage.withPercentage(99.9));
            }
        }

        @Nested
        @DisplayName("bookLine의 lineCategory가 지출이 아닌 경우")
        class Context_With_BookLineNotOutcome {

            final Book book = BookFixture.createBook();
            final DateDuration dateDuration = new DateDuration(LocalDate.now(), LocalDate.now());

            @BeforeEach
            void init() {
                bookRepository.save(book);
                final User user = userRepository.save(UserFixture.emailUser());
                final BookUser bookUser = bookUserRepository.save(BookUser.of(user, book));

                final Subcategory incomeSubcategory = subcategoryRepository.save(Subcategory.of(incomeCategory, book, "급여"));
                final Subcategory assetSubcategory = subcategoryRepository.save(Subcategory.of(assetCategory, book, "현금"));

                final List<BookLine> bookLines = List.of(
                    BookLineFixture.createWithDate(
                        book, bookUser, categories(incomeCategory, incomeSubcategory, assetSubcategory), LocalDate.now()
                    )
                );
                bookLineRepository.saveAll(bookLines);
            }

            @Test
            @DisplayName("0.0을 반환한다.")
            void it_returns_double() {
                assertThat(bookLineRepository.totalOutcomeMoneyForBudget(book, dateDuration))
                    .isCloseTo(0.0, Percentage.withPercentage(99.9));
            }
        }

        @Nested
        @DisplayName("해당 기간에 bookLine이 존재하지 않는 경우")
        class Context_With_NoBookLineInDuration {

            final Book book = BookFixture.createBook();
            final DateDuration dateDuration = new DateDuration(LocalDate.now(), LocalDate.now());

            @BeforeEach
            void init() {
                bookRepository.save(book);
                final User user = userRepository.save(UserFixture.emailUser());
                final BookUser bookUser = bookUserRepository.save(BookUser.of(user, book));

                final Subcategory outcomeSubcategory = subcategoryRepository.save(Subcategory.of(outcomeCategory, book, "식비"));
                final Subcategory assetSubcategory = subcategoryRepository.save(Subcategory.of(assetCategory, book, "현금"));

                final List<BookLine> bookLines = List.of(
                    BookLineFixture.createWithDateNotBudget(
                        book, bookUser, categories(outcomeCategory, outcomeSubcategory, assetSubcategory), LocalDate.now().plusDays(1)
                    )
                );
                bookLineRepository.saveAll(bookLines);
            }

            @Test
            @DisplayName("0.0을 반환한다.")
            void it_returns_double() {
                assertThat(bookLineRepository.totalOutcomeMoneyForBudget(book, dateDuration))
                    .isCloseTo(0.0, Percentage.withPercentage(99.9));
            }
        }
    }

    @Nested
    @DisplayName("totalExpensesForAsset()를 실행할 때")
    class Describe_TotalExpensesForAsset {

        final Category incomeCategory = categoryRepository.findByType(INCOME).orElseThrow();
        final Category outcomeCategory = categoryRepository.findByType(OUTCOME).orElseThrow();
        final Category assetCategory = categoryRepository.findByType(ASSET).orElseThrow();
        final Category transferCategory = categoryRepository.findByType(TRANSFER).orElseThrow();

        @Nested
        @DisplayName("해당 달에 수입 또는 지출인 bookLine이 존재하는 경우")
        class Context_With_IncomeOrOutcomeBookLine {

            final Book book = BookFixture.createBook();
            final LocalDate date = LocalDate.now();

            @BeforeEach
            void init() {
                bookRepository.save(book);
                final User user = userRepository.save(UserFixture.emailUser());
                final BookUser bookUser = bookUserRepository.save(BookUser.of(user, book));

                final Subcategory incomeSubcategory = subcategoryRepository.save(Subcategory.of(incomeCategory, book, "급여"));
                final Subcategory outcomeSubcategory = subcategoryRepository.save(Subcategory.of(outcomeCategory, book, "식비"));
                final Subcategory assetSubcategory = subcategoryRepository.save(Subcategory.of(assetCategory, book, "현금"));

                final List<BookLine> bookLines = List.of(
                    BookLineFixture.createWithDate(
                        book, bookUser, categories(incomeCategory, incomeSubcategory, assetSubcategory), date
                    ),
                    BookLineFixture.createWithDate(
                        book, bookUser, categories(incomeCategory, incomeSubcategory, assetSubcategory), date
                    ),
                    BookLineFixture.createWithDate(
                        book, bookUser, categories(outcomeCategory, outcomeSubcategory, assetSubcategory), date
                    ),
                    BookLineFixture.createWithDate(
                        book, bookUser, categories(outcomeCategory, outcomeSubcategory, assetSubcategory), date
                    )
                );
                bookLineRepository.saveAll(bookLines);
            }

            @Test
            @DisplayName("수입과 지출의 총합을 반환한다.")
            void it_returns_map() {
                assertThat(bookLineRepository.totalExpensesForAsset(book, date))
                    .hasSize(2)
                    .containsEntry(INCOME.getMeaning(), DEFAULT_MONEY * 2)
                    .containsEntry(OUTCOME.getMeaning(), DEFAULT_MONEY * 2);
            }
        }

        @Nested
        @DisplayName("bookLine이 지출 또는 수입이 아닌 경우")
        class Context_With_BookLineNotIncomeOrOutcome {

            final Book book = BookFixture.createBook();
            final LocalDate date = LocalDate.now();

            @BeforeEach
            void init() {
                bookRepository.save(book);
                final User user = userRepository.save(UserFixture.emailUser());
                final BookUser bookUser = bookUserRepository.save(BookUser.of(user, book));

                final Subcategory transferSubcategory = subcategoryRepository.save(Subcategory.of(incomeCategory, book, "이체"));
                final Subcategory assetSubcategory = subcategoryRepository.save(Subcategory.of(assetCategory, book, "현금"));

                final List<BookLine> bookLines = List.of(
                    BookLineFixture.createWithDate(
                        book, bookUser, categories(transferCategory, transferSubcategory, assetSubcategory), date
                    )
                );
                bookLineRepository.saveAll(bookLines);
            }

            @Test
            @DisplayName("0.0인 수입과 지출의 총합을 반환한다.")
            void it_returns_map() {
                assertThat(bookLineRepository.totalExpensesForAsset(book, date))
                    .hasSize(2)
                    .containsEntry(INCOME.getMeaning(), 0.0)
                    .containsEntry(OUTCOME.getMeaning(), 0.0);
            }
        }
    }

    @Nested
    @DisplayName("findAllByBookKeyOrderByDateDesc()를 실행할 때")
    class Describe_FindAllByBookKeyOrderByDateDesc {

        final Category incomeCategory = categoryRepository.findByType(INCOME).orElseThrow();
        final Category outcomeCategory = categoryRepository.findByType(OUTCOME).orElseThrow();
        final Category transferCategory = categoryRepository.findByType(TRANSFER).orElseThrow();
        final Category assetCategory = categoryRepository.findByType(ASSET).orElseThrow();

        @Nested
        @DisplayName("bookKey에 해당하는 bookLine이 존재하는 경우")
        class Context_With_BookLineByBookKey {

            final String bookKey = "AAAAAA";

            @BeforeEach
            void init() {
                final Book book = bookRepository.save(BookFixture.createBookWith(bookKey));
                final User user = userRepository.save(UserFixture.emailUser());
                final BookUser bookUser = bookUserRepository.save(BookUser.of(user, book));

                final Subcategory incomeSubcategory = subcategoryRepository.save(Subcategory.of(incomeCategory, book, "급여"));
                final Subcategory outcomeSubcategory = subcategoryRepository.save(Subcategory.of(outcomeCategory, book, "식비"));
                final Subcategory transferSubcategory = subcategoryRepository.save(Subcategory.of(transferCategory, book, "이체"));
                final Subcategory assetSubcategory = subcategoryRepository.save(Subcategory.of(assetCategory, book, "현금"));

                final List<BookLine> bookLines = List.of(
                    BookLineFixture.createWithDate(
                        book, bookUser, categories(incomeCategory, incomeSubcategory, assetSubcategory), LocalDate.now()
                    ),
                    BookLineFixture.createWithDate(
                        book, bookUser, categories(outcomeCategory, outcomeSubcategory, assetSubcategory), LocalDate.now()
                    ),
                    BookLineFixture.createWithDate(
                        book, bookUser, categories(transferCategory, transferSubcategory, assetSubcategory), LocalDate.now()
                    )
                );
                bookLineRepository.saveAll(bookLines);
            }

            @Test
            @DisplayName("date로 내림차순 정렬된 BookLine 목록을 반환한다.")
            void it_returns_bookLines() {
                assertThat(bookLineRepository.findAllByBookKeyOrderByDateDesc(bookKey))
                    .hasSize(3)
                    .isSortedAccordingTo(Comparator.comparing(BookLine::getLineDate).reversed());
            }
        }
    }

    @Nested
    @DisplayName("totalMoneyByDurationAndCategoryType()를 실행할 때")
    class Describe_TotalMoneyByDurationAndCategoryType {

        final Category incomeCategory = categoryRepository.findByType(INCOME).orElseThrow();
        final Category assetCategory = categoryRepository.findByType(ASSET).orElseThrow();

        @Nested
        @DisplayName("특정 lineCategory를 가진 bookLine들이 해당 기간에 존재하는 경우")
        class Context_With_BookLineByDurationAndCategoryType {

            final String bookKey = "AAAAAA";
            final DateDuration dateDuration = new DateDuration(LocalDate.now(), LocalDate.now());

            double totalMoney;

            @BeforeEach
            void init() {
                final Book book = bookRepository.save(BookFixture.createBookWith(bookKey));
                final User user = userRepository.save(UserFixture.emailUser());
                final BookUser bookUser = bookUserRepository.save(BookUser.of(user, book));

                final Subcategory incomeSubcategory = subcategoryRepository.save(Subcategory.of(incomeCategory, book, "급여"));
                final Subcategory assetSubcategory = subcategoryRepository.save(Subcategory.of(assetCategory, book, "현금"));

                final List<BookLine> bookLines = List.of(
                    BookLineFixture.createWithDate(
                        book, bookUser, categories(incomeCategory, incomeSubcategory, assetSubcategory), LocalDate.now()
                    ),
                    BookLineFixture.createWithDate(
                        book, bookUser, categories(incomeCategory, incomeSubcategory, assetSubcategory), LocalDate.now()
                    )
                );
                bookLineRepository.saveAll(bookLines);

                totalMoney = bookLines.stream().mapToDouble(BookLine::getMoney).sum();
            }

            @Test
            @DisplayName("총합을 반환한다.")
            void it_returns_double() {
                assertThat(bookLineRepository.totalMoneyByDurationAndCategoryType(bookKey, dateDuration, INCOME))
                    .isCloseTo(totalMoney, Percentage.withPercentage(99.9));
            }
        }

        @Nested
        @DisplayName("해당 날짜의 bookLine들이 다른 lineCategory를 가지는 경우")
        class Context_With_BookLineWithDifferentCategoryByDay {

            final String bookKey = "AAAAAA";
            final DateDuration dateDuration = new DateDuration(LocalDate.now(), LocalDate.now());

            @BeforeEach
            void init() {
                final Book book = bookRepository.save(BookFixture.createBookWith(bookKey));
                final User user = userRepository.save(UserFixture.emailUser());
                final BookUser bookUser = bookUserRepository.save(BookUser.of(user, book));

                final Subcategory lineSubcategory = subcategoryRepository.save(Subcategory.of(incomeCategory, book, "급여"));
                final Subcategory assetSubcategory = subcategoryRepository.save(Subcategory.of(assetCategory, book, "현금"));

                final List<BookLine> bookLines = List.of(
                    BookLineFixture.createWithDate(
                        book, bookUser, categories(incomeCategory, lineSubcategory, assetSubcategory), LocalDate.now()
                    )
                );
                bookLineRepository.saveAll(bookLines);
            }

            @Test
            @DisplayName("0.0을 반환한다.")
            void it_returns_double() {
                assertThat(bookLineRepository.totalMoneyByDurationAndCategoryType(bookKey, dateDuration, OUTCOME))
                    .isCloseTo(0.0, Percentage.withPercentage(99.9));
            }
        }
    }

    @Nested
    @DisplayName("getCarryOver()를 실행할 때")
    class Describe_GetCarryOver {

        final Category incomeCategory = categoryRepository.findByType(INCOME).orElseThrow();
        final Category assetCategory = categoryRepository.findByType(ASSET).orElseThrow();

        @Nested
        @DisplayName("가계부 내역이 주어지면")
        class Context_With_BookLine {

            final String bookKey = "AAAAAA";
            final DateDuration dateDuration = new DateDuration(LocalDate.now(), LocalDate.now());

            double totalMoney;

            @BeforeEach
            void init() {

            }

            @Test
            @DisplayName("총수입을 반환한다.")
            void it_returns_double() {

            }
        }

        @Nested
        @DisplayName("해당 날짜의 bookLine들이 다른 lineCategory를 가지는 경우")
        class Context_With_BookLineWithDifferentCategoryByDay {

            final String bookKey = "AAAAAA";
            final DateDuration dateDuration = new DateDuration(LocalDate.now(), LocalDate.now());

            @BeforeEach
            void init() {
                final Book book = bookRepository.save(BookFixture.createBookWith(bookKey));
                final User user = userRepository.save(UserFixture.emailUser());
                final BookUser bookUser = bookUserRepository.save(BookUser.of(user, book));

                final Subcategory lineSubcategory = subcategoryRepository.save(Subcategory.of(incomeCategory, book, "급여"));
                final Subcategory assetSubcategory = subcategoryRepository.save(Subcategory.of(assetCategory, book, "현금"));

                final List<BookLine> bookLines = List.of(
                    BookLineFixture.createWithDate(
                        book, bookUser, categories(incomeCategory, lineSubcategory, assetSubcategory), LocalDate.now()
                    )
                );
                bookLineRepository.saveAll(bookLines);
            }

            @Test
            @DisplayName("0.0을 반환한다.")
            void it_returns_double() {
                assertThat(bookLineRepository.totalMoneyByDurationAndCategoryType(bookKey, dateDuration, OUTCOME))
                    .isCloseTo(0.0, Percentage.withPercentage(99.9));
            }
        }
    }

    private BookLineCategory categories(final Category lineCategory,
                                        final Subcategory lineSubcategory,
                                        final Subcategory assetSubcategory) {
        return BookLineCategory.create(lineCategory, lineSubcategory, assetSubcategory);
    }
}
