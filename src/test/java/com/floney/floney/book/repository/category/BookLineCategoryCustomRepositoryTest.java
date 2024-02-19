package com.floney.floney.book.repository.category;

import com.floney.floney.book.domain.category.entity.Category;
import com.floney.floney.book.domain.category.entity.Subcategory;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookLine;
import com.floney.floney.book.domain.entity.BookLineCategory;
import com.floney.floney.book.domain.entity.BookUser;
import com.floney.floney.book.repository.BookLineRepository;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.config.QueryDslTest;
import com.floney.floney.fixture.BookFixture;
import com.floney.floney.fixture.BookUserFixture;
import com.floney.floney.fixture.UserFixture;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static com.floney.floney.book.domain.category.CategoryType.ASSET;
import static com.floney.floney.book.domain.category.CategoryType.INCOME;

@QueryDslTest
class BookLineCategoryCustomRepositoryTest {


    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookUserRepository bookUserRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SubcategoryRepository subcategoryRepository;

    @Autowired
    private BookLineRepository bookLineRepository;

    @Autowired
    private BookLineCategoryRepository bookLineCategoryRepository;

    private BookUser bookUser;
    private User user;
    private Book book;
    private BookLine bookLine1;
    private BookLine bookLine2;
    private Category incomeLineCategory;
    private Category assetLineCategory;
    private BookLineCategory bookLineCategory;
    private BookLineCategory bookLineCategory2;
    private Subcategory subCategoryForLine;
    private Subcategory subCategoryForAsset;

    @BeforeEach
    void init() {
        book = bookRepository.save(BookFixture.createBook());
        user = userRepository.save(UserFixture.emailUser());
        bookUser = bookUserRepository.save(BookUserFixture.createBookUser(book, user));

        incomeLineCategory = categoryRepository.findLineCategory(INCOME).get();
        assetLineCategory = categoryRepository.findLineCategory(ASSET).get();

        subCategoryForLine = Subcategory.builder()
            .book(book)
            .parent(incomeLineCategory)
            .name("급여")
            .build();

        subCategoryForAsset = Subcategory.builder()
            .book(book)
            .name("은행")
            .parent(assetLineCategory)
            .build();

        subcategoryRepository.save(subCategoryForLine);
        subcategoryRepository.save(subCategoryForAsset);

        bookLineCategory = BookLineCategory.create(incomeLineCategory, subCategoryForLine, subCategoryForAsset);
        bookLineCategory2 = BookLineCategory.create(incomeLineCategory, subCategoryForLine, subCategoryForAsset);

        bookLine1 = BookLine.builder()
            .book(book)
            .lineDate(LocalDate.now())
            .writer(bookUser)
            .description("예시")
            .categories(bookLineCategory)
            .build();

        bookLine2 = BookLine.builder()
            .book(book)
            .lineDate(LocalDate.now())
            .writer(bookUser)
            .description("예시2")
            .categories(bookLineCategory2)
            .build();

        bookLineCategory.bookLine(bookLine1);
        bookLineCategory2.bookLine(bookLine2);

        bookLineRepository.save(bookLine1);
        bookLineRepository.save(bookLine2);

    }

    private void assertFindBookLineCategoryIsEmpty() {
        Assertions.assertThat(bookLineCategoryRepository.findById(bookLineCategory.getId()).isEmpty());
        Assertions.assertThat(bookLineCategoryRepository.findById(bookLineCategory2.getId()).isEmpty());
    }

    @Nested
    @DisplayName("inactiveAllBy 메서드에서")
    class Describe_InactiveAllByBookKey {

        @Nested
        @DisplayName("하나의 가계부에 다수의 bookLineCategory 주어지고")
        class bookLine {
            @Nested
            @DisplayName("가계부키로 모든 bookLineCategory를 비활성화 시키면")
            class inactive {
                @BeforeEach
                void inactive_all() {
                    bookLineCategoryRepository.inactiveAllBy(book);
                }

                @Test
                @DisplayName("가계부의 모든 bookLineCategory를 찾을 수 없다")
                void assert_empty() {
                    assertFindBookLineCategoryIsEmpty();
                }
            }
        }
    }

    @Nested
    @DisplayName("inactiveAllByBookLineId 메서드에서")
    class Describe_InactiveAllByBookLineId {
        @Nested
        @DisplayName("가계부 내역 Id로 bookLineCategory를 비활성화 시키면")
        class inactive {
            @BeforeEach
            void inactive_all() {
                bookLineCategoryRepository.inactiveAllByBookLineId(bookLine1.getId());
                bookLineCategoryRepository.inactiveAllByBookLineId(bookLine2.getId());
            }

            @Test
            @DisplayName("가계부 내역 Id로 bookLineCategory를 찾을 수 없다")
            void assert_empty() {
                assertFindBookLineCategoryIsEmpty();
            }
        }
    }

    @Nested
    @DisplayName("inactiveAllByBookUser 메서드에서")
    class Describe_InactiveAllByBookUser {
        @Nested
        @DisplayName("BookUser와 연결된 bookLineCategory를 모두 비활성화 시키면")
        class inactive {
            @BeforeEach
            void inactive_all() {
                bookLineCategoryRepository.inactiveAllByBookUser(bookUser);
            }

            @Test
            @DisplayName("bookLineCategory를 찾을 수 없다")
            void assert_empty() {
                assertFindBookLineCategoryIsEmpty();
            }
        }
    }

    @Nested
    @DisplayName("inactiveAllBy 메서드에서")
    class Describe_InactiveAllByBook {
        @Nested
        @DisplayName("book과 연관된 bookLineCategory를 모두 비활성화 시키면")
        class inactive {
            @BeforeEach
            void inactive_all() {
                bookLineCategoryRepository.inactiveAllBy(book);
            }

            @Test
            @DisplayName("bookLineCategory를 찾을 수 없다")
            void find_none() {
                assertFindBookLineCategoryIsEmpty();
            }
        }
    }
}
