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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static com.floney.floney.book.domain.category.CategoryType.ASSET;
import static com.floney.floney.book.domain.category.CategoryType.INCOME;
import static org.assertj.core.api.Assertions.assertThat;

@QueryDslTest
@DisplayName("단위 테스트: BookLineCategoryCustomRepository")
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
    private Book book;
    private BookLine bookLine1;

    @BeforeEach
    void init() {
        book = bookRepository.save(BookFixture.createBook());
        final User user = userRepository.save(UserFixture.emailUser());
        bookUser = bookUserRepository.save(BookUserFixture.createBookUser(book, user));

        final Category incomeLineCategory = categoryRepository.findByType(INCOME).orElseThrow();
        final Category assetLineCategory = categoryRepository.findByType(ASSET).orElseThrow();

        final Subcategory subCategoryForLine = Subcategory.builder()
            .book(book)
            .parent(incomeLineCategory)
            .name("급여")
            .build();

        final Subcategory subCategoryForAsset = Subcategory.builder()
            .book(book)
            .name("은행")
            .parent(assetLineCategory)
            .build();

        subcategoryRepository.save(subCategoryForLine);
        subcategoryRepository.save(subCategoryForAsset);

        final BookLineCategory bookLineCategory = BookLineCategory.create(incomeLineCategory, subCategoryForLine, subCategoryForAsset);
        final BookLineCategory bookLineCategory2 = BookLineCategory.create(incomeLineCategory, subCategoryForLine, subCategoryForAsset);

        bookLine1 = BookLine.builder()
            .book(book)
            .lineDate(LocalDate.now())
            .writer(bookUser)
            .description("예시")
            .categories(bookLineCategory)
            .build();

        final BookLine bookLine2 = BookLine.builder()
            .book(book)
            .lineDate(LocalDate.now())
            .writer(bookUser)
            .description("예시2")
            .categories(bookLineCategory2)
            .build();

        bookLineRepository.save(bookLine1);
        bookLineRepository.save(bookLine2);
    }

    @Nested
    @DisplayName("inactiveAllByBook()를 실행할 때")
    class Describe_InactiveAllByBook {

        @Nested
        @DisplayName("해당 Book에 bookLineCategory가 있으면")
        class Context_With_BookLineCategoryByBook {

            @Test
            @DisplayName("모두 비활성화 한다.")
            void it_inactivates_all() {
                bookLineCategoryRepository.inactiveAllByBook(book);

                final List<BookLineCategory> activeBookLineCategories = bookLineCategoryRepository.findAll()
                    .stream()
                    .filter(blc -> blc.getBookLine().getBook().equals(book) && blc.isActive())
                    .toList();

                assertThat(activeBookLineCategories).isEmpty();
            }
        }
    }

    @Nested
    @DisplayName("inactiveAllByBookLineId()를 실행할 때")
    class Describe_InactiveAllByBookLineId {

        @Nested
        @DisplayName("해당 bookLineId를 가지는 bookLineCategory가 있으면")
        class Context_With_InactiveAll {

            @Test
            @DisplayName("모두 비활성화 한다.")
            void it_inactivates_all() {
                bookLineCategoryRepository.inactiveAllByBookLineId(bookLine1.getId());

                final List<BookLineCategory> activeBookLineCategories = bookLineCategoryRepository.findAll()
                    .stream()
                    .filter(blc -> blc.getBookLine().getId().equals(bookLine1.getId()) && blc.isActive())
                    .toList();

                assertThat(activeBookLineCategories).isEmpty();
            }
        }
    }

    @Nested
    @DisplayName("inactiveAllByBookUser()를 실행할 때")
    class Describe_InactiveAllByBookUser {

        @Nested
        @DisplayName("해당 bookUser를 가지는 bookLineCategory가 있으면")
        class Context_With_InactiveAllByUser {

            @Test
            @DisplayName("모두 비활성화 한다.")
            void it_inactivates_all() {
                bookLineCategoryRepository.inactiveAllByBookUser(bookUser);

                final List<BookLineCategory> activeBookLineCategories = bookLineCategoryRepository.findAll()
                    .stream()
                    .filter(blc -> blc.getBookLine().getWriterNickName().equals(bookUser.getNickName()) && blc.isActive())
                    .toList();

                assertThat(activeBookLineCategories).isEmpty();
            }
        }
    }
}
