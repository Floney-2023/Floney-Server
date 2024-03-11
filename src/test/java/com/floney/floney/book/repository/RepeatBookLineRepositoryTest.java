package com.floney.floney.book.repository;


import com.floney.floney.book.domain.category.entity.Category;
import com.floney.floney.book.domain.category.entity.Subcategory;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookUser;
import com.floney.floney.book.domain.entity.RepeatBookLine;
import com.floney.floney.book.repository.category.CategoryRepository;
import com.floney.floney.book.repository.category.SubcategoryRepository;
import com.floney.floney.config.QueryDslTest;
import com.floney.floney.fixture.BookFixture;
import com.floney.floney.fixture.RepeatBookLineFixture;
import com.floney.floney.fixture.UserFixture;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.floney.floney.book.domain.RepeatDuration.EVERYDAY;
import static com.floney.floney.book.domain.RepeatDuration.MONTH;
import static com.floney.floney.book.domain.category.CategoryType.ASSET;
import static com.floney.floney.book.domain.category.CategoryType.INCOME;
import static com.floney.floney.common.constant.Status.ACTIVE;
import static com.floney.floney.fixture.RepeatBookLineFixture.createRepeatBookLine;
import static com.floney.floney.fixture.SubcategoryFixture.createSubcategory;
import static org.assertj.core.api.Assertions.assertThat;

@QueryDslTest
@DisplayName("단위 테스트: RepeatBookLineRepository")
public class RepeatBookLineRepositoryTest {

    @Autowired
    private RepeatBookLineRepository repeatBookLineRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookUserRepository bookUserRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private SubcategoryRepository subcategoryRepository;

    @Nested
    @DisplayName("inactiveAllBy()를 실행할 때")
    class Describe_InactiveAllBy {

        @Nested
        @DisplayName("반복 내역이 존재하는 경우")
        class Context_With_RepeatBookLine {

            Book book;
            Category category;

            @BeforeEach
            void init() {
                book = bookRepository.save(BookFixture.createBook());

                final User user = userRepository.save(UserFixture.emailUser());
                final BookUser bookUser = bookUserRepository.save(BookUser.of(user, book));
                category = categoryRepository.findByType(INCOME).orElseThrow();

                final RepeatBookLine repeatBookLine = createRepeatBookLine(category, bookUser, EVERYDAY);
                final RepeatBookLine repeatBookLine2 = createRepeatBookLine(category, bookUser, MONTH);

                repeatBookLineRepository.save(repeatBookLine);
                repeatBookLineRepository.save(repeatBookLine2);
            }

            @Test
            @DisplayName("반복 내역을 모두 비활성화 시킨다.")
            void it_inactivates_all() {
                repeatBookLineRepository.inactiveAllByBook(book);

                assertThat(repeatBookLineRepository.findAllByBookAndStatusAndLineCategory(book, ACTIVE, category))
                    .isEmpty();
            }
        }
    }

    @Nested
    @DisplayName("inactiveAllByBookUser()를 실행할 때")
    class Describe_InactiveAllByBookUser {

        @Nested
        @DisplayName("반복 내역이 존재하는 경우")
        class Context_With_RepeatBookLine {

            Book book;
            BookUser bookUser;
            Category category;

            @BeforeEach
            void init() {
                book = bookRepository.save(BookFixture.createBook());

                final User user = userRepository.save(UserFixture.emailUser());
                bookUser = bookUserRepository.save(BookUser.of(user, book));
                category = categoryRepository.findByType(INCOME).orElseThrow();

                final RepeatBookLine repeatBookLine = createRepeatBookLine(category, bookUser, EVERYDAY);
                final RepeatBookLine repeatBookLine2 = createRepeatBookLine(category, bookUser, MONTH);

                repeatBookLineRepository.save(repeatBookLine);
                repeatBookLineRepository.save(repeatBookLine2);
            }

            @Test
            @DisplayName("반복 내역을 모두 비활성화 시킨다.")
            void it_inactivates_all() {
                repeatBookLineRepository.inactiveAllByBookUser(bookUser);

                assertThat(repeatBookLineRepository.findAllByBookAndStatusAndLineCategory(book, ACTIVE, category))
                    .isEmpty();
            }
        }
    }

    @Nested
    @DisplayName("inactiveAllBySubcategory()를 실행할 때")
    class Describe_InactiveAllBySubcategory {

        @Nested
        @DisplayName("lineSubcategory 로 요청하는 경우")
        class Context_With_LineSubcategory {

            Subcategory lineSubcategory;

            @BeforeEach
            void init() {
                final User user = userRepository.save(UserFixture.emailUser());
                final Book book = bookRepository.save(BookFixture.createBook());
                final BookUser bookUser = bookUserRepository.save(BookUser.of(user, book));

                final Category incomeCategory = categoryRepository.findByType(INCOME).orElseThrow();
                final Category assetCategory = categoryRepository.findByType(ASSET).orElseThrow();

                lineSubcategory = subcategoryRepository.save(createSubcategory(book, incomeCategory, "급여"));

                final RepeatBookLine repeatBookLine = RepeatBookLineFixture.repeatBookLine(
                    MONTH,
                    bookUser,
                    lineSubcategory,
                    subcategoryRepository.save(createSubcategory(book, assetCategory, "은행"))
                );
                repeatBookLineRepository.save(repeatBookLine);
            }

            @Test
            @DisplayName("반복 내역을 모두 비활성화한다.")
            void it_inactivates_all() {
                repeatBookLineRepository.inactiveAllBySubcategory(lineSubcategory);

                assertThat(repeatBookLineRepository.findAllBySubcategory(lineSubcategory)).isEmpty();
            }
        }

        @Nested
        @DisplayName("assetSubcategory 로 요청하는 경우")
        class Context_With_AssetSubcategory {

            Subcategory assetSubcategory;

            @BeforeEach
            void init() {
                final User user = userRepository.save(UserFixture.emailUser());
                final Book book = bookRepository.save(BookFixture.createBook());
                final BookUser bookUser = bookUserRepository.save(BookUser.of(user, book));

                final Category incomeCategory = categoryRepository.findByType(INCOME).orElseThrow();
                final Category assetCategory = categoryRepository.findByType(ASSET).orElseThrow();

                assetSubcategory = subcategoryRepository.save(createSubcategory(book, incomeCategory, "은행"));

                final RepeatBookLine repeatBookLine = RepeatBookLineFixture.repeatBookLine(
                    MONTH,
                    bookUser,
                    subcategoryRepository.save(createSubcategory(book, assetCategory, "급여")),
                    assetSubcategory
                );
                repeatBookLineRepository.save(repeatBookLine);
            }

            @Test
            @DisplayName("반복 내역을 모두 비활성화한다.")
            void it_inactivates_all() {
                repeatBookLineRepository.inactiveAllBySubcategory(assetSubcategory);

                assertThat(repeatBookLineRepository.findAllBySubcategory(assetSubcategory)).isEmpty();
            }
        }
    }
}
