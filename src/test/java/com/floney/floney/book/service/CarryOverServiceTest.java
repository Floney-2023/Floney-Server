package com.floney.floney.book.service;

import com.floney.floney.analyze.service.CarryOverServiceImpl;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookLine;
import com.floney.floney.book.domain.entity.BookLineCategory;
import com.floney.floney.book.domain.entity.BookUser;
import com.floney.floney.book.repository.analyze.CarryOverRepository;
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

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("단위 테스트 : CarryOverService")
@ExtendWith(MockitoExtension.class)
public class CarryOverServiceTest {

    @InjectMocks
    private CarryOverServiceImpl carryOverService;

    @Mock
    private CarryOverRepository carryOverRepository;

    @Nested
    @DisplayName("createCarryOver()를 실행할 때")
    class Describe_CreateCarryOver {
        User user;
        Book book;
        String bookKey;
        BookUser bookUser;

        BookLine bookLine;
        String assetSubCategoryName = "자산";

        @BeforeEach()
        void init() {
            user = UserFixture.emailUser();
            book = BookFixture.createBook();
            bookKey = book.getBookKey();
            bookUser = BookUserFixture.createBookUser(book, user);
        }

        @Nested
        @DisplayName("카테고리가 수입인 가계부 내역이 주어지면")
        class Context_With_IncomeBookLine {
            @BeforeEach
            void init() {
                String incomeSubCategoryName = "급여";
                final BookLineCategory bookLineCategory = BookLineCategoryFixture.incomeBookLineCategory(book, incomeSubCategoryName, assetSubCategoryName);
                bookLine = BookLineFixture.createWithDate(book, bookUser, bookLineCategory, LocalDate.now());
            }

            @Test
            @DisplayName("이월 내역이 생성 된다")
            void it_create_carryOver() {
                carryOverService.createCarryOver(bookLine);
                verify(carryOverRepository, times(1)).saveAll(any(List.class));
            }
        }

        @Nested
        @DisplayName("카테고리가 지출인 가계부 내역이 주어지면")
        class Context_With_OutcomeBookLine {

            @BeforeEach
            void init() {
                String outcomeSubCategoryName = "식비";
                final BookLineCategory bookLineCategory = BookLineCategoryFixture.outcomeBookLineCategory(book, outcomeSubCategoryName, assetSubCategoryName);
                bookLine = BookLineFixture.createWithDate(book, bookUser, bookLineCategory, LocalDate.now());
            }

            @Test
            @DisplayName("이월 내역이 생성 된다")
            void it_create_carryOver() {
                carryOverService.createCarryOver(bookLine);
                verify(carryOverRepository, times(1)).saveAll(any(List.class));
            }
        }

        @Nested
        @DisplayName("카테고리가 이체인 가계부 내역이 주어지면")
        class Context_With_TransferBookLine {
            @BeforeEach
            void init() {
                String transferSubCategoryName = "이체";
                final BookLineCategory bookLineCategory = BookLineCategoryFixture.transferBookLineCategory(book, transferSubCategoryName, assetSubCategoryName);
                bookLine = BookLineFixture.createWithDate(book, bookUser, bookLineCategory, LocalDate.now());
            }

            @Test
            @DisplayName("이월 내역이 생성 되지 않는다")
            void it_didnt_create_carryOver() {
                carryOverService.createCarryOver(bookLine);
                verify(carryOverRepository, never()).saveAll(any(List.class));
            }
        }
    }
}
