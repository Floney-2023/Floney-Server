package com.floney.floney.book;


import com.floney.floney.book.domain.entity.*;
import com.floney.floney.fixture.*;
import com.floney.floney.user.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

@DisplayName("단위 테스트 : CarryOver")
public class CarryOverTest {

    @Nested
    @DisplayName("Of()를 실행할 때")
    class Describe_Of {
        User user;
        Book book;
        String bookKey;
        BookUser bookUser;

        BookLine bookLine;
        String assetSubCategoryName = "자산";

        @BeforeEach
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
            @DisplayName("금액이 양수인, 이월 내역이 생성된다")
            void it_return_positive_money() {
                CarryOver carryOver = CarryOver.of(bookLine, LocalDate.now().plusDays(1));
                Assertions.assertThat(carryOver.getMoney()).isEqualTo(bookLine.getMoney());
            }

        }

        @Nested
        @DisplayName("카테고리가 지출인 가계부 내역이 주어지면")
        class Context_With_OutcomeBookLine {

            @BeforeEach
            void init() {
                String outcomeSubCategory = "급여";
                final BookLineCategory bookLineCategory = BookLineCategoryFixture.outcomeBookLineCategory(book, outcomeSubCategory, assetSubCategoryName);
                bookLine = BookLineFixture.createWithDate(book, bookUser, bookLineCategory, LocalDate.now());
            }

            @Test
            @DisplayName("금액이 음수인, 이월 내역이 생성된다")
            void it_return_negative_money() {
                CarryOver carryOver = CarryOver.of(bookLine, LocalDate.now().plusDays(1));
                Assertions.assertThat(carryOver.getMoney()).isEqualTo(-1 * bookLine.getMoney());
            }

        }
    }
}
