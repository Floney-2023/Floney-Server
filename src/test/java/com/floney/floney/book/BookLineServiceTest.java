package com.floney.floney.book;

import com.floney.floney.analyze.service.AssetServiceImpl;
import com.floney.floney.book.domain.RepeatDuration;
import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.book.domain.category.entity.Category;
import com.floney.floney.book.domain.category.entity.Subcategory;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookLine;
import com.floney.floney.book.domain.entity.BookLineCategory;
import com.floney.floney.book.domain.entity.BookUser;
import com.floney.floney.book.dto.request.BookLineRequest;
import com.floney.floney.book.dto.response.BookLineResponse;
import com.floney.floney.book.repository.BookLineRepository;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.book.repository.category.CategoryRepository;
import com.floney.floney.book.service.BookLineServiceImpl;
import com.floney.floney.common.constant.Status;
import com.floney.floney.fixture.*;
import com.floney.floney.user.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

//TODO : 1. 테스트 코드 전체 추가, 2. mock 안쓰는 방법으로 개선
@ExtendWith(MockitoExtension.class)
public class BookLineServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookLineRepository bookLineRepository;

    @Mock
    private BookUserRepository bookUserRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private BookLineServiceImpl bookLineService;

    @Mock
    private AssetServiceImpl assetService;

    @Nested
    @DisplayName("createBookLine()을 실행할 때")
    class Describe_CreateBookLine {
        User user;
        Book book;
        String bookKey;
        BookUser bookUser;
        String incomeSubCategoryName = "급여";
        String assetSubCategoryName = "자산";

        @Nested
        @DisplayName("작성할 내역이 주어진 경우")
        class Context_With_BookLine {

            @BeforeEach
            public void init() {
                user = UserFixture.emailUser();
                book = BookFixture.createBook();
                bookKey = book.getBookKey();
                bookUser = BookUserFixture.createBookUser(book, user);

                Category incomeCategory = CategoryFixture.incomeCategory();
                Category assetCategory = CategoryFixture.assetCategory();
                Subcategory subCategory = SubcategoryFixture.createSubcategory(book, incomeCategory, incomeSubCategoryName);
                Subcategory assetSubCategory = SubcategoryFixture.createSubcategory(book, assetCategory, assetSubCategoryName);
                final BookLineCategory bookLineCategory = BookLineCategoryFixture.incomeBookLineCategory(book, incomeSubCategoryName, assetSubCategoryName);

                given(categoryRepository.findByType(CategoryType.INCOME))
                    .willReturn(Optional.ofNullable(incomeCategory));
                given(bookRepository.findBookByBookKeyAndStatus(bookKey, Status.ACTIVE))
                    .willReturn(Optional.ofNullable(book));
                given(bookUserRepository.findBookUserByEmailAndBookKey(user.getEmail(), bookKey))
                    .willReturn(Optional.ofNullable(bookUser));
                given(categoryRepository.findSubcategory(incomeSubCategoryName, book, CategoryType.INCOME))
                    .willReturn(Optional.ofNullable(subCategory));
                given(categoryRepository.findSubcategory(assetSubCategoryName, book, CategoryType.ASSET))
                    .willReturn(Optional.ofNullable(assetSubCategory));
                given(bookLineRepository.save(any(BookLine.class)))
                    .willReturn(BookLineFixture.createWithDate(book, bookUser, bookLineCategory, LocalDate.now()));
            }

            @Test
            @DisplayName("가계부 내역이 생성된다")
            void it_returns_bookLine() {
                final BookLineRequest request = BookLineRequest.builder()
                    .bookKey(bookKey)
                    .flow(CategoryType.INCOME.getMeaning())
                    .line(incomeSubCategoryName)
                    .asset(assetSubCategoryName)
                    .lineDate(LocalDate.now())
                    .except(Boolean.FALSE)
                    .money(2000)
                    .repeatDuration(RepeatDuration.NONE)
                    .build();

                BookLineResponse bookLineResponse = bookLineService.createBookLine(user.getEmail(), request);
                Assertions.assertThat(bookLineResponse.getLineDate())
                    .isEqualTo(LocalDate.now());
            }
        }
    }

}
