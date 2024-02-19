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
import com.floney.floney.fixture.SubCategoryFixture;
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
@DisplayName("단위테스트 : CategoryCustomRepository")
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

    private Subcategory incomeSubCategory(String name) {
        Subcategory subCategoryForIncome1 = SubCategoryFixture.createSubcategory(book, incomeLineCategory, name);
        return subcategoryRepository.save(subCategoryForIncome1);
    }

    private void outcomeSubCategory(String name) {
        Subcategory subCategoryForIncome1 = SubCategoryFixture.createSubcategory(book, outcomeLineCategory, name);
        subcategoryRepository.save(subCategoryForIncome1);
    }

    private Subcategory assetSubCategory(String name) {
        Subcategory subCategoryForIncome1 = SubCategoryFixture.createSubcategory(book, assetLineCategory, name);
        return subcategoryRepository.save(subCategoryForIncome1);
    }

    private void transferSubCategory(String name) {
        Subcategory subCategoryForIncome1 = SubCategoryFixture.createSubcategory(book, transferLineCategory, name);
        subcategoryRepository.save(subCategoryForIncome1);
    }

    @Nested
    @DisplayName("findAllSubCategoryByCategoryType()를 실행할 때")
    class Describe_FindAllSubCategoryType {
        @Nested
        @DisplayName("INCOME이 부모인 SubCategory가 주어진다면")
        class Context_With_Income {
            private static final String incomeSubCategoryName1 = "급여";
            private static final String incomeSubCategoryName2 = "용돈";

            @BeforeEach
            void init() {
                incomeSubCategory(incomeSubCategoryName1);
                Subcategory subCategoryForIncome2 = SubCategoryFixture.createSubcategory(book, incomeLineCategory, incomeSubCategoryName2);
                subcategoryRepository.save(subCategoryForIncome2);
            }

            @Test
            @DisplayName("INCOME을 부모로 가지는 모든 자식 카테고리를 조회할 수 있다")
            void find_by_income() {
                List<CategoryInfo> subCategories = categoryRepository.findAllSubCategoryInfoByParent(INCOME, book.getBookKey());

                Assertions.assertThat(subCategories.size()).isEqualTo(2);
                Assertions.assertThat(subCategories)
                    .extracting(CategoryInfo::getName)
                    .containsExactlyInAnyOrder(incomeSubCategoryName1, incomeSubCategoryName2);
            }
        }

        @Nested
        @DisplayName("OUTCOME이 부모인 SubCategory가 주어진다면")
        class outcome {
            private static final String outcomeSubCategoryName1 = "식비";
            private static final String outcomeSubCategoryName2 = "경조사";

            @BeforeEach
            void save_sub_category() {
                outcomeSubCategory(outcomeSubCategoryName1);
                Subcategory subCategoryForOutcome2 = SubCategoryFixture.createSubcategory(book, outcomeLineCategory, outcomeSubCategoryName2);
                subcategoryRepository.save(subCategoryForOutcome2);

            }

            @Test
            @DisplayName("OUTCOME을 부모로 가지는 모든 자식 카테고리를 조회할 수 있다")
            void find_by_outcome() {
                List<CategoryInfo> subCategories = categoryRepository.findAllSubCategoryInfoByParent(CategoryType.OUTCOME, book.getBookKey());

                Assertions.assertThat(subCategories.size()).isEqualTo(2);
                Assertions.assertThat(subCategories)
                    .extracting(CategoryInfo::getName)
                    .containsExactlyInAnyOrder(outcomeSubCategoryName1, outcomeSubCategoryName2);
            }
        }

        @Nested
        @DisplayName("ASSET이 부모인 SubCategory가 주어진다면")
        class asset {
            private static final String assetSubCategoryName1 = "신용카드";
            private static final String assetSubCategoryName2 = "현금";

            @BeforeEach
            void save_sub_category() {
                Subcategory subCategoryForAsset1 = SubCategoryFixture.createSubcategory(book, assetLineCategory, assetSubCategoryName1);
                Subcategory subCategoryForAsset2 = SubCategoryFixture.createSubcategory(book, assetLineCategory, assetSubCategoryName2);

                subcategoryRepository.save(subCategoryForAsset1);
                subcategoryRepository.save(subCategoryForAsset2);
            }

            @Test
            @DisplayName("ASSET을 부모로 가지는 모든 자식 카테고리를 조회할 수 있다")
            void find_by_asset() {
                List<CategoryInfo> subCategories = categoryRepository.findAllSubCategoryInfoByParent(ASSET, book.getBookKey());

                Assertions.assertThat(subCategories.size()).isEqualTo(2);
                Assertions.assertThat(subCategories)
                    .extracting(CategoryInfo::getName)
                    .containsExactlyInAnyOrder(assetSubCategoryName1, assetSubCategoryName2);
            }
        }

        @Nested
        @DisplayName("TRANSFER가 부모인 SubCategory가 주어진다면")
        class transfer {
            private static final String transferSubCategoryName1 = "은행";
            private static final String transferSubCategoryName2 = "이체";

            @BeforeEach
            void save_sub_category() {
                transferSubCategory(transferSubCategoryName1);
                transferSubCategory(transferSubCategoryName2);
            }

            @Test
            @DisplayName("TRANSFER를 부모로 가지는 모든 자식 카테고리를 조회할 수 있다")
            void find_by_transfer() {
                List<CategoryInfo> subCategories = categoryRepository.findAllSubCategoryInfoByParent(CategoryType.TRANSFER, book.getBookKey());

                Assertions.assertThat(subCategories.size()).isEqualTo(2);
                Assertions.assertThat(subCategories)
                    .extracting(CategoryInfo::getName)
                    .containsExactlyInAnyOrder(transferSubCategoryName1, transferSubCategoryName2);
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
            void save_sub_category() {
                incomeSubCategory(categoryName);
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
            void inactive_by_parent() {
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
            void save_sub_category() {
                outcomeSubCategory(categoryName);
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
            void inactive_by_parent() {
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
            void save_sub_category() {
                assetSubCategory(categoryName);
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
            void inactive_by_parent() {
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
            void save_sub_category() {
                transferSubCategory(categoryName);
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
            void inactive_by_parent() {
                Optional<Category> parent = categoryRepository.findLineCategory(TRANSFER);
                parent.get().inactive();
                Assertions.assertThat(categoryRepository.findLineSubCategory(categoryName, book, TRANSFER)).isEmpty();
            }
        }
    }

    @Nested
    @DisplayName("findAllBookLineByCategory 메서드에서")
    class Describe_FindAllBookLineByCategory {
        private Subcategory subCategoryForLine;
        private Subcategory subCategoryForAsset;

        @Nested
        @DisplayName("가계부 내역이 주어진다면")
        class bookLine {
            private static final String incomeCategoryName = "급여";
            private static final String assetCategoryName = "체크카드";

            @BeforeEach
            void save_bookLine() {
                User user = userRepository.save(UserFixture.emailUser());
                BookUser newBookUser = BookUser.builder()
                    .book(book)
                    .user(user)
                    .build();
                BookUser bookUser = bookUserRepository.save(newBookUser);

                subCategoryForLine = incomeSubCategory(incomeCategoryName);
                subCategoryForAsset = assetSubCategory(assetCategoryName);

                // parent
                incomeLineCategory = categoryRepository.findLineCategory(CategoryType.INCOME).get();
                assetLineCategory = categoryRepository.findLineCategory(CategoryType.ASSET).get();

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

        @Nested
        @DisplayName("사용자가 추가한 커스텀 카테고리가 주어진다면")
        class customCategory {
            private static final String customCategoryName = "사용자가 추가한 카테고리";

            @BeforeEach
            void custom_category() {
                Subcategory customCategory = SubCategoryFixture.createSubcategory(book, assetLineCategory, customCategoryName);
                subcategoryRepository.save(customCategory);
            }

            @Test
            @DisplayName("카테고리 이름으로 조회할 수 있다")
            void find_custom_category() {
                Assertions.assertThat(categoryRepository.findCustomCategory(assetLineCategory, book, customCategoryName)).isNotEmpty();
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

                String assetSubCategoryName1 = "신용카드";
                subCategoryForAsset1 = assetSubCategory(assetSubCategoryName1);

                String assetSubCategoryName2 = "체크카드";
                subCategoryForAsset2 = assetSubCategory(assetSubCategoryName2);
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
    @DisplayName("inactiveAllBy 메서드에서")
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
