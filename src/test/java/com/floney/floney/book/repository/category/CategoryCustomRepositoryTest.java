package com.floney.floney.book.repository.category;

import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.book.domain.category.entity.Category;
import com.floney.floney.book.domain.category.entity.Subcategory;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.dto.process.CategoryInfo;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.config.QueryDslTest;
import com.floney.floney.fixture.BookFixture;
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
    private CategoryRepository categoryRepository;

    @Autowired
    private SubcategoryRepository subcategoryRepository;

    private Book book;
    private Category incomeLineCategory;
    private Category outcomeLineCategory;
    private Category assetLineCategory;
    private Category transferLineCategory;

    @BeforeEach
    void init() {
        book = bookRepository.save(BookFixture.createBook());
        incomeLineCategory = categoryRepository.findLineCategory(CategoryType.INCOME).get();
        assetLineCategory = categoryRepository.findLineCategory(CategoryType.ASSET).get();
        outcomeLineCategory = categoryRepository.findLineCategory(CategoryType.OUTCOME).get();
        transferLineCategory = categoryRepository.findLineCategory(CategoryType.TRANSFER).get();
    }

    @Nested
    @DisplayName("findAllSubCategoryByCategoryType 메서드에서")
    class Describe_FindAllSubCategory {

        @Nested
        @DisplayName("INCOME이 부모인 SubCategory가 주어진다면")
        class income {
            @BeforeEach
            void saveSubCategory() {
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
            }

            @Test
            @DisplayName("INCOME을 부모로 가지는 모든 자식 카테고리를 조회할 수 있다")
            void find_by_income() {
                List<CategoryInfo> subCategories = categoryRepository.findAllSubCategoryByParent(CategoryType.INCOME, book.getBookKey());

                Assertions.assertThat(subCategories.size()).isEqualTo(2);
                Assertions.assertThat(subCategories)
                    .extracting(CategoryInfo::getName)
                    .containsExactlyInAnyOrder("급여", "용돈");
            }
        }

        @Nested
        @DisplayName("OUTCOME이 부모인 SubCategory가 주어진다면")
        class outcome {
            @BeforeEach
            void saveSubCategory() {
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
            }

            @Test
            @DisplayName("OUTCOME을 부모로 가지는 모든 자식 카테고리를 조회할 수 있다")
            void find_by_outcome() {
                List<CategoryInfo> subCategories = categoryRepository.findAllSubCategoryByParent(CategoryType.OUTCOME, book.getBookKey());


                Assertions.assertThat(subCategories.size()).isEqualTo(2);
                Assertions.assertThat(subCategories)
                    .extracting(CategoryInfo::getName)
                    .containsExactlyInAnyOrder("식비", "경조사");
            }
        }

        @Nested
        @DisplayName("ASSET이 부모인 SubCategory가 주어진다면")
        class asset {
            @BeforeEach
            void saveSubCategory() {
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
            }

            @Test
            @DisplayName("ASSET을 부모로 가지는 모든 자식 카테고리를 조회할 수 있다")
            void find_by_asset() {
                List<CategoryInfo> subCategories = categoryRepository.findAllSubCategoryByParent(CategoryType.ASSET, book.getBookKey());

                // then
                Assertions.assertThat(subCategories.size()).isEqualTo(2);
                Assertions.assertThat(subCategories)
                    .extracting(CategoryInfo::getName)
                    .containsExactlyInAnyOrder("현금", "체크카드");
            }
        }

        @Nested
        @DisplayName("TRANSFER가 부모인 SubCategory가 주어진다면")
        class transfer {
            @BeforeEach
            void saveSubCategory() {
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

            }

            @Test
            @DisplayName("TRANSFER를 부모로 가지는 모든 자식 카테고리를 조회할 수 있다")
            void find_by_transfer() {
                List<CategoryInfo> subCategories = categoryRepository.findAllSubCategoryByParent(CategoryType.TRANSFER, book.getBookKey());

                // then
                Assertions.assertThat(subCategories.size()).isEqualTo(2);
                Assertions.assertThat(subCategories)
                    .extracting(CategoryInfo::getName)
                    .containsExactlyInAnyOrder("은행", "이체");
            }
        }

    }

    @Nested
    @DisplayName("findLineCategory 메서드에서")
    class Describe_FindLineCategory {

        @Test
        @DisplayName("ASSET을 조회한다")
        void find_asset() {
            Assertions.assertThat(categoryRepository.findLineCategory(CategoryType.ASSET)).isNotEmpty();
        }

        @Test
        @DisplayName("INCOME을 조회한다")
        void find_income() {
            Assertions.assertThat(categoryRepository.findLineCategory(CategoryType.INCOME)).isNotEmpty();
        }

        @Test
        @DisplayName("TRANSFER을 조회한다")
        void find_transfer() {
            Assertions.assertThat(categoryRepository.findLineCategory(CategoryType.TRANSFER)).isNotEmpty();
        }

        @Test
        @DisplayName("OUTCOME을 조회한다")
        void find_outcome() {
            Assertions.assertThat(categoryRepository.findLineCategory(CategoryType.OUTCOME)).isNotEmpty();
        }
    }
}
