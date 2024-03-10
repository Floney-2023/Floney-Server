package com.floney.floney.book.domain.entity;


import com.floney.floney.fixture.*;
import com.floney.floney.user.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

@DisplayName("단위 테스트: 가계부 내역")
public class BookLineTest {

    @Nested
    @DisplayName("isIncludedInAsset()를 실행할 때")
    class Describe_IsIncludedInAsset {

        User user;
        Book book;
        BookUser bookUser;
        String assetSubCategoryName = "자산";

        @BeforeEach
        void init() {
            user = UserFixture.emailUser();
            book = BookFixture.createBook();
            bookUser = BookUserFixture.createBookUser(book, user);
        }
        
        @Nested
        @DisplayName("지출 내역이 주어지면")
        class Context_OutcomeBookLine {

            BookLine bookLine;

            @BeforeEach
            void init() {
                String outcomeSubCategoryName = "식비";
                final BookLineCategory bookLineCategory = BookLineCategoryFixture.outcomeBookLineCategory(book, outcomeSubCategoryName, assetSubCategoryName);
                bookLine = BookLineFixture.createWithDate(book, bookUser, bookLineCategory, LocalDate.now());
            }

            @Test
            @DisplayName("True를 반환한다")
            void it_returns_true() {
                Assertions.assertThat(bookLine.isIncludedInAsset()).isTrue();
            }
        }

        @Nested
        @DisplayName("자산에서 제외하지 않은 수입 내역이 주어지면")
        class Context_NotExceptIncomeBookLine {

            BookLine bookLine;

            @BeforeEach
            void init() {
                String incomeSubCategoryName = "급여";
                final BookLineCategory bookLineCategory = BookLineCategoryFixture.incomeBookLineCategory(book, incomeSubCategoryName, assetSubCategoryName);
                bookLine = BookLineFixture.createWithDate(book, bookUser, bookLineCategory, LocalDate.now());
            }

            @Test
            @DisplayName("True를 반환한다")
            void it_returns_true() {
                Assertions.assertThat(bookLine.isIncludedInAsset()).isTrue();
            }
        }


        @Nested
        @DisplayName("자산에서 제외한 수입 내역이 주어지면")
        class Context_ExceptIncomeBookLine {

            BookLine bookLine;

            @BeforeEach
            void init() {
                String incomeSubCategoryName = "급여";
                final BookLineCategory bookLineCategory = BookLineCategoryFixture.incomeBookLineCategory(book, incomeSubCategoryName, assetSubCategoryName);
                bookLine = BookLineFixture.createWithDate(book, bookUser, bookLineCategory, LocalDate.now());
                ReflectionTestUtils.setField(bookLine, "exceptStatus", true);
            }

            @Test
            @DisplayName("false를 반환한다")
            void it_returns_false() {
                Assertions.assertThat(bookLine.isIncludedInAsset()).isFalse();
            }
        }


    }
}
