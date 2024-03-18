package com.floney.floney.book.repository.category;

import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.book.domain.category.entity.Category;
import com.floney.floney.book.domain.category.entity.Subcategory;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.config.QueryDslTest;
import com.floney.floney.fixture.BookFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThatNoException;

@QueryDslTest
@DisplayName("단위 테스트: SubcategoryRepository")
class SubcategoryRepositoryTest {

    @Autowired
    private SubcategoryRepository subcategoryRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Nested
    @DisplayName("inactive()를 실행할 때")
    class Describe_Inactive {

        @Nested
        @DisplayName("이미 INACTIVE 상태인 Subcategory가 존재하는 경우")
        class Context_With_InactiveSubcategory {

            Subcategory subcategory;

            @BeforeEach
            void init() {
                final Category category = categoryRepository.findByType(CategoryType.INCOME).orElseThrow();
                final Book book = BookFixture.createBook();
                ReflectionTestUtils.setField(book, "id", 1L);

                final String subcategoryName = "새 카테고리";

                final Subcategory inactiveSubcategory = Subcategory.of(category, book, subcategoryName);
                inactiveSubcategory.inactive();
                subcategoryRepository.save(inactiveSubcategory);

                subcategory = Subcategory.of(category, book, subcategoryName);
                subcategoryRepository.save(subcategory);
            }

            @Test
            @DisplayName("Subcategory를 삭제한다.")
            void it_inactivates_subcategory() {
                assertThatNoException()
                    .isThrownBy(() -> subcategoryRepository.inactive(subcategory));
            }
        }

        @Nested
        @DisplayName("INACTIVE 상태인 Subcategory가 존재하지 않는 경우")
        class Context_With_NoInactiveSubcategory {

            Subcategory subcategory;

            @BeforeEach
            void init() {
                final Category category = categoryRepository.findByType(CategoryType.INCOME).orElseThrow();
                final Book book = BookFixture.createBook();
                ReflectionTestUtils.setField(book, "id", 1L);

                subcategory = Subcategory.of(category, book, "새 카테고리");
                subcategoryRepository.save(subcategory);
            }

            @Test
            @DisplayName("Subcategory를 삭제한다.")
            void it_inactivates_subcategory() {
                assertThatNoException()
                    .isThrownBy(() -> subcategoryRepository.inactive(subcategory));
            }
        }
    }
}
