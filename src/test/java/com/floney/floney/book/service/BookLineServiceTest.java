package com.floney.floney.book.service;

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
import com.floney.floney.common.constant.Status;
import com.floney.floney.fixture.*;
import com.floney.floney.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

//TODO : 1. 테스트 코드 전체 추가, 2. mock 안쓰는 방법으로 개선
@ExtendWith(MockitoExtension.class)
@DisplayName("단위 테스트: BookLineService")
public class BookLineServiceTest {

    @InjectMocks
    private BookLineServiceImpl bookLineService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookLineRepository bookLineRepository;

    @Mock
    private BookUserRepository bookUserRepository;

    @Mock
    private CategoryRepository categoryRepository;
    
    @Nested
    @DisplayName("createBookLine()을 실행할 때")
    class Describe_CreateBookLine {

        @Nested
        @DisplayName("작성할 내역이 주어진 경우")
        class Context_With_BookLine {

            String userEmail;
            String bookKey;

            @BeforeEach
            public void init() {
                final User user = UserFixture.emailUser();
                userEmail = user.getEmail();
                final Book book = BookFixture.createBook();
                bookKey = book.getBookKey();
                final BookUser bookUser = BookUserFixture.createBookUser(book, user);

                final Category incomeCategory = CategoryFixture.create(CategoryType.INCOME);
                final Category assetCategory = CategoryFixture.create(CategoryType.ASSET);
                final Subcategory subCategory = SubcategoryFixture.createSubcategory(book, incomeCategory, "급여");
                final Subcategory assetSubCategory = SubcategoryFixture.createSubcategory(book, assetCategory, "현금");
                final BookLineCategory bookLineCategory = BookLineCategory.create(incomeCategory, subCategory, assetSubCategory);

                given(categoryRepository.findByType(CategoryType.INCOME))
                    .willReturn(Optional.ofNullable(incomeCategory));

                given(bookRepository.findBookByBookKeyAndStatus(bookKey, Status.ACTIVE))
                    .willReturn(Optional.of(book));

                given(bookUserRepository.findBookUserByEmailAndBookKey(userEmail, bookKey))
                    .willReturn(Optional.ofNullable(bookUser));

                given(categoryRepository.findSubcategory("급여", book, CategoryType.INCOME))
                    .willReturn(Optional.ofNullable(subCategory));

                given(categoryRepository.findSubcategory("현금", book, CategoryType.ASSET))
                    .willReturn(Optional.ofNullable(assetSubCategory));

                final BookLine bookLine = BookLineFixture.create(book, bookUser, bookLineCategory);
                ReflectionTestUtils.setField(bookLine, "id", 1L);
                given(bookLineRepository.save(any(BookLine.class)))
                    .willReturn(bookLine);
            }

            @Test
            @DisplayName("생성한 내역의 정보를 반환한다.")
            void it_returns_bookLine() {
                final BookLineRequest request = BookLineRequest.builder()
                    .bookKey(bookKey)
                    .flow(CategoryType.INCOME.getMeaning())
                    .line("급여")
                    .asset("현금")
                    .lineDate(LocalDate.now())
                    .except(Boolean.FALSE)
                    .money(2000)
                    .repeatDuration(RepeatDuration.NONE)
                    .build();

                final BookLineResponse bookLineResponse = bookLineService.createBookLine(userEmail, request);
                assertThat(bookLineResponse.getId()).isEqualTo(1);
            }
        }
    }
}
