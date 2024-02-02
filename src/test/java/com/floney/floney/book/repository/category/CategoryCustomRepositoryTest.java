package com.floney.floney.book.repository.category;

import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.book.domain.category.entity.Category;
import com.floney.floney.book.domain.category.entity.Subcategory;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookLine;
import com.floney.floney.book.domain.entity.BookLineCategory;
import com.floney.floney.book.domain.entity.BookUser;
import com.floney.floney.book.dto.process.CategoryInfo;
import com.floney.floney.book.repository.BookLineRepository;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.floney.floney.book.domain.category.CategoryType.*;

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

    @Autowired
    private BookLineRepository bookLineRepository;

    private Book book;
    private Category incomeLineCategory;
    private Category outcomeLineCategory;
    private Category assetLineCategory;
    private Category transferLineCategory;

    @BeforeEach
    void init() {
        book = bookRepository.save(BookFixture.createBook());
        incomeLineCategory = categoryRepository.findLineCategory(INCOME).get();
        assetLineCategory = categoryRepository.findLineCategory(ASSET).get();
        outcomeLineCategory = categoryRepository.findLineCategory(CategoryType.OUTCOME).get();
        transferLineCategory = categoryRepository.findLineCategory(CategoryType.TRANSFER).get();
    }

    @Nested
    @DisplayName("findAllSubCategoryByCategoryType 메서드에서")
    class Describe_FindAllSubCategoryType {
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
                List<CategoryInfo> subCategories = categoryRepository.findAllSubCategoryInfoByParent(INCOME, book.getBookKey());

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
                List<CategoryInfo> subCategories = categoryRepository.findAllSubCategoryInfoByParent(CategoryType.OUTCOME, book.getBookKey());

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
                List<CategoryInfo> subCategories = categoryRepository.findAllSubCategoryInfoByParent(ASSET, book.getBookKey());

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
                List<CategoryInfo> subCategories = categoryRepository.findAllSubCategoryInfoByParent(CategoryType.TRANSFER, book.getBookKey());

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
            Assertions.assertThat(categoryRepository.findLineCategory(ASSET)).isNotEmpty();
        }

        @Test
        @DisplayName("INCOME을 조회한다")
        void find_income() {
            Assertions.assertThat(categoryRepository.findLineCategory(INCOME)).isNotEmpty();
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

    @Nested
    @DisplayName("findLineSubCategory 메서드에서")
    class Describe_FindLineSubCategory {
        @Nested
        @DisplayName("INCOME이 부모인 SubCategory가 주어지고")
        class income {
            private final String categoryName = "급여";

            @BeforeEach
            void saveSubCategory() {
                Subcategory subCategoryForLine1 = Subcategory.builder()
                    .book(book)
                    .parent(incomeLineCategory)
                    .name(categoryName)
                    .build();

                subcategoryRepository.save(subCategoryForLine1);
            }

            @Test
            @DisplayName("가계부, 카테고리가 모두 활성화 되어있다면, 이름으로 카테고리를 조회할 수 있다")
            void find_by() {
                Assertions.assertThat(categoryRepository.findLineSubCategory(categoryName, book, INCOME)).isNotEmpty();
            }

            @Test
            @DisplayName("가계부가 비활성화 되었다면 , 카테고리를 조회할 수 없다")
            void inactive_book() {
                book.inactive();
                Assertions.assertThat(categoryRepository.findLineSubCategory(categoryName, book, INCOME)).isEmpty();
            }

            @Test
            @DisplayName("부모 카테고리가 비활성화 되었다면 , 카테고리를 조회할 수 없다")
            void inactive_parent() {
                Optional<Category> parent = categoryRepository.findLineCategory(INCOME);
                parent.get().inactive();
                Assertions.assertThat(categoryRepository.findLineSubCategory(categoryName, book, INCOME)).isEmpty();
            }
        }

        @Nested
        @DisplayName("OUTCOME이 부모인 SubCategory가 주어진다면")
        class outcome {
            private final String categoryName = "식비";

            @BeforeEach
            void saveSubCategory() {
                Subcategory subCategoryForLine1 = Subcategory.builder()
                    .book(book)
                    .parent(outcomeLineCategory)
                    .name(categoryName)
                    .build();

                subcategoryRepository.save(subCategoryForLine1);
            }

            @Test
            @DisplayName("가계부, 카테고리가 모두 활성화 되어있다면, 이름으로 카테고리를 조회할 수 있다")
            void find_by() {
                Assertions.assertThat(categoryRepository.findLineSubCategory(categoryName, book, OUTCOME)).isNotEmpty();
            }

            @Test
            @DisplayName("가계부가 비활성화 되었다면 , 카테고리를 조회할 수 없다")
            void inactive_book() {
                book.inactive();
                Assertions.assertThat(categoryRepository.findLineSubCategory(categoryName, book, OUTCOME)).isEmpty();
            }

            @Test
            @DisplayName("부모 카테고리가 비활성화 되었다면 , 카테고리를 조회할 수 없다")
            void inactive_parent() {
                Optional<Category> parent = categoryRepository.findLineCategory(OUTCOME);
                parent.get().inactive();
                Assertions.assertThat(categoryRepository.findLineSubCategory(categoryName, book, OUTCOME)).isEmpty();
            }
        }

        @Nested
        @DisplayName("ASSET이 부모인 SubCategory가 주어진다면")
        class asset {
            private final String categoryName = "체크카드";

            @BeforeEach
            void saveSubCategory() {
                Subcategory subCategoryForLine1 = Subcategory.builder()
                    .book(book)
                    .parent(assetLineCategory)
                    .name(categoryName)
                    .build();

                subcategoryRepository.save(subCategoryForLine1);
            }

            @Test
            @DisplayName("가계부, 카테고리가 모두 활성화 되어있다면, 이름으로 카테고리를 조회할 수 있다")
            void find_by() {
                Assertions.assertThat(categoryRepository.findLineSubCategory(categoryName, book, ASSET)).isNotEmpty();
            }

            @Test
            @DisplayName("가계부가 비활성화 되었다면 , 카테고리를 조회할 수 없다")
            void inactive_book() {
                book.inactive();
                Assertions.assertThat(categoryRepository.findLineSubCategory(categoryName, book, ASSET)).isEmpty();
            }

            @Test
            @DisplayName("부모 카테고리가 비활성화 되었다면 , 카테고리를 조회할 수 없다")
            void inactive_parent() {
                Optional<Category> parent = categoryRepository.findLineCategory(ASSET);
                parent.get().inactive();
                Assertions.assertThat(categoryRepository.findLineSubCategory(categoryName, book, ASSET)).isEmpty();
            }
        }

        @Nested
        @DisplayName("TRANSFER가 부모인 SubCategory가 주어진다면")
        class transfer {
            private final String categoryName = "이체";

            @BeforeEach
            void saveSubCategory() {
                Subcategory subCategoryForLine1 = Subcategory.builder()
                    .book(book)
                    .parent(transferLineCategory)
                    .name(categoryName)
                    .build();

                subcategoryRepository.save(subCategoryForLine1);
            }

            @Test
            @DisplayName("가계부, 카테고리가 모두 활성화 되어있다면, 이름으로 카테고리를 조회할 수 있다")
            void find_by() {
                Assertions.assertThat(categoryRepository.findLineSubCategory(categoryName, book, TRANSFER)).isNotEmpty();
            }

            @Test
            @DisplayName("가계부가 비활성화 되었다면 , 카테고리를 조회할 수 없다")
            void inactive_book() {
                book.inactive();
                Assertions.assertThat(categoryRepository.findLineSubCategory(categoryName, book, TRANSFER)).isEmpty();
            }

            @Test
            @DisplayName("부모 카테고리가 비활성화 되었다면 , 카테고리를 조회할 수 없다")
            void inactive_parent() {
                Optional<Category> parent = categoryRepository.findLineCategory(TRANSFER);
                parent.get().inactive();
                Assertions.assertThat(categoryRepository.findLineSubCategory(categoryName, book, TRANSFER)).isEmpty();
            }
        }
    }

    @Nested
    @DisplayName("findAllBookLineByCategory 메서드에서")
    class Describe_FindAllBookLineByCategory {
        private BookUser bookUser;
        private User user;
        private Subcategory subCategoryForLine;
        private Subcategory subCategoryForAsset;

        @Nested
        @DisplayName("가계부 내역이 주어진다면")
        class bookLine {
            @BeforeEach
            void save_bookLine() {
                user = userRepository.save(UserFixture.emailUser());
                BookUser newBookUser = BookUser.builder()
                    .book(book)
                    .user(user)
                    .build();
                bookUser = bookUserRepository.save(newBookUser);

                incomeLineCategory = categoryRepository.findLineCategory(CategoryType.INCOME).get();
                assetLineCategory = categoryRepository.findLineCategory(CategoryType.ASSET).get();

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

                BookLineCategory bookLineCategory = BookLineCategory.create(incomeLineCategory, subCategoryForLine, subCategoryForAsset);
                BookLineCategory bookLineCategory2 = BookLineCategory.create(incomeLineCategory, subCategoryForLine, subCategoryForAsset);

                BookLine bookLine1 = BookLine.builder()
                    .book(book)
                    .lineDate(LocalDate.now())
                    .writer(bookUser)
                    .description("예시")
                    .categories(bookLineCategory)
                    .build();

                BookLine bookLine2 = BookLine.builder()
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

            @Test
            @DisplayName("SubCategory 모든 가계부 내역을 조회할 수 있다")
            void find_all() {
                Assertions.assertThat(categoryRepository.findAllBookLineBySubCategory(subCategoryForAsset).size()).isEqualTo(2);
                Assertions.assertThat(categoryRepository.findAllBookLineBySubCategory(subCategoryForLine).size()).isEqualTo(2);
            }
        }
    }

    @Nested
    @DisplayName("findCustomCategory 메서드에서")
    class Describe_FindCustomCategory {
        private Subcategory customCategory;

        @Nested
        @DisplayName("사용자가 추가한 커스텀 카테고리가 주어진다면")
        class customCategory {
            @BeforeEach
            void custom_category() {
                customCategory = Subcategory.builder()
                    .book(book)
                    .name("사용자가 추가한 카테고리")
                    .parent(assetLineCategory)
                    .build();

                subcategoryRepository.save(customCategory);
            }

            @Test
            @DisplayName("카테고리 이름으로 조회할 수 있다")
            void find_custom_category() {
                Assertions.assertThat(categoryRepository.findCustomCategory(assetLineCategory, book, customCategory.getName())).isNotEmpty();
            }
        }
    }

    @Nested
    @DisplayName("findAllSubCategoryByLineCategory 메서드에서")
    class Describe_findAll {
        @Nested
        @DisplayName("가계부 내역이 주어진다면")
        class bookLine {
            private Subcategory subCategoryForAsset1;
            private Subcategory subCategoryForAsset2;

            @BeforeEach
            void save_bookLine() {
                assetLineCategory = categoryRepository.findLineCategory(CategoryType.ASSET).get();

                subCategoryForAsset1 = Subcategory.builder()
                    .book(book)
                    .name("은행")
                    .parent(assetLineCategory)
                    .build();

                subCategoryForAsset2 = Subcategory.builder()
                    .book(book)
                    .name("체크카드")
                    .parent(assetLineCategory)
                    .build();

                subcategoryRepository.save(subCategoryForAsset1);
                subcategoryRepository.save(subCategoryForAsset2);
            }

            @Test
            @DisplayName("부모 카테고리로 모든 자식 카테고리를 조회할 수 있다")
            void find_all() {
                Assertions.assertThat(categoryRepository.findAllSubCategoryByLineCategory(assetLineCategory, book.getBookKey()))
                    .extracting(Subcategory::getName)
                    .containsExactlyInAnyOrder(subCategoryForAsset1.getName(), subCategoryForAsset2.getName());
            }
        }
    }

    @Nested
    @DisplayName("inactiveAllByBook 메서드에서")
    class Describe_InactiveAllByBook {
        @Nested
        @DisplayName("가계부와 연결된 모든 자식 카테고리를 삭제하면")
        class inactive {
            @BeforeEach
            void inactive_all() {
                categoryRepository.inactiveAllByBook(book);
            }

            @Test
            @DisplayName("가계부의 카테고리가 조회되지 않는다")
            void find_none() {
                Assertions.assertThat(categoryRepository.findAllSubCategoryByLineCategory(assetLineCategory, book.getBookKey())).isEmpty();
            }
        }


    }

}
