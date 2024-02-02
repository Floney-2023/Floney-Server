package com.floney.floney.book.repository.category;

import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.book.domain.category.entity.Category;
import com.floney.floney.book.domain.category.entity.Subcategory;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookUser;
import com.floney.floney.book.dto.process.CategoryInfo;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.config.QueryDslTest;
import com.floney.floney.fixture.BookFixture;
import com.floney.floney.fixture.UserFixture;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@QueryDslTest
class CategoryCustomRepositoryTest {

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

    private User user;
    private Book book;
    private BookUser bookUser;
    private Category incomeLineCategory;
    private Category outcomeLineCategory;
    private Category assetLineCategory;
    private Category transferLineCategory;

    @BeforeEach
    void init() {
        user = userRepository.save(UserFixture.emailUser());
        book = bookRepository.save(BookFixture.createBook());
        BookUser newBookUser = BookUser.builder()
            .book(book)
            .user(user)
            .build();
        bookUser = bookUserRepository.save(newBookUser);

        incomeLineCategory = categoryRepository.findLineCategory(CategoryType.INCOME).get();
        assetLineCategory = categoryRepository.findLineCategory(CategoryType.ASSET).get();
        outcomeLineCategory = categoryRepository.findLineCategory(CategoryType.OUTCOME).get();
        transferLineCategory = categoryRepository.findLineCategory(CategoryType.TRANSFER).get();
    }

    @Nested
    @DisplayName("findAllSubCategoryByCategoryType 메서드에서")
    class Describe_FindAllSubCategory {
        @Test
        @DisplayName("INCOME을 부모로 가지는 모든 subCategory를 조회한다")
        void find_by_income() {
            // given
            Subcategory subCategoryForLine1 = Subcategory.builder()
                .book(book)
                .parent(incomeLineCategory)
                .name("급여")
                .build();

            Subcategory subCategoryForLine2 = Subcategory.builder()
                .book(book)
                .parent(incomeLineCategory)
                .name("용돈")
                .build();
            subcategoryRepository.save(subCategoryForLine1);
            subcategoryRepository.save(subCategoryForLine2);

            // when
            List<CategoryInfo> subCategories = categoryRepository.findAllSubCategoryByCategoryType(CategoryType.INCOME, book.getBookKey());

            // then
            Assertions.assertThat(subCategories.size()).isEqualTo(2);
            Assertions.assertThat(subCategories)
                .extracting(CategoryInfo::getName)
                .containsExactlyInAnyOrder("급여", "용돈");
        }

        @Test
        @DisplayName("OUTCOME 부모로 가지는 모든 subCategory를 조회한다")
        void find_by_outcome() {
            // given
            Subcategory subCategoryForLine1 = Subcategory.builder()
                .book(book)
                .parent(outcomeLineCategory)
                .name("식비")
                .build();

            Subcategory subCategoryForLine2 = Subcategory.builder()
                .book(book)
                .parent(outcomeLineCategory)
                .name("경조사")
                .build();
            subcategoryRepository.save(subCategoryForLine1);
            subcategoryRepository.save(subCategoryForLine2);

            // when
            List<CategoryInfo> subCategories = categoryRepository.findAllSubCategoryByCategoryType(CategoryType.OUTCOME, book.getBookKey());

            // then
            Assertions.assertThat(subCategories.size()).isEqualTo(2);
            Assertions.assertThat(subCategories)
                .extracting(CategoryInfo::getName)
                .containsExactlyInAnyOrder("식비", "경조사");
        }

        @Test
        @DisplayName("ASSET을 부모로 가지는 모든 subCategory를 조회한다")
        void find_by_asset() {
            // given
            Subcategory subCategoryForLine1 = Subcategory.builder()
                .book(book)
                .parent(assetLineCategory)
                .name("현금")
                .build();

            Subcategory subCategoryForLine2 = Subcategory.builder()
                .book(book)
                .parent(assetLineCategory)
                .name("체크카드")
                .build();
            subcategoryRepository.save(subCategoryForLine1);
            subcategoryRepository.save(subCategoryForLine2);

            // when
            List<CategoryInfo> subCategories = categoryRepository.findAllSubCategoryByCategoryType(CategoryType.ASSET, book.getBookKey());

            // then
            Assertions.assertThat(subCategories.size()).isEqualTo(2);
            Assertions.assertThat(subCategories)
                .extracting(CategoryInfo::getName)
                .containsExactlyInAnyOrder("현금", "체크카드");
        }

        @Test
        @DisplayName("Transfer를 부모로 가지는 모든 subCategory를 조회한다")
        void find_by_transfer() {
            // given
            Subcategory subCategoryForLine1 = Subcategory.builder()
                .book(book)
                .parent(transferLineCategory)
                .name("은행")
                .build();

            Subcategory subCategoryForLine2 = Subcategory.builder()
                .book(book)
                .parent(transferLineCategory)
                .name("이체")
                .build();
            subcategoryRepository.save(subCategoryForLine1);
            subcategoryRepository.save(subCategoryForLine2);

            // when
            List<CategoryInfo> subCategories = categoryRepository.findAllSubCategoryByCategoryType(CategoryType.TRANSFER, book.getBookKey());

            // then
            Assertions.assertThat(subCategories.size()).isEqualTo(2);
            Assertions.assertThat(subCategories)
                .extracting(CategoryInfo::getName)
                .containsExactlyInAnyOrder("은행", "이체");
        }
    }
}
