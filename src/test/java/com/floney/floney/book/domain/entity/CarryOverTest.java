package com.floney.floney.book.domain.entity;


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
    @DisplayName("getCarryOverToAdd()를 실행할 때")
    class Describe_GetCarryOverToAdd {
        User user;
        Book book;
        String bookKey;
        BookUser bookUser;
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

            BookLine bookLine;

            @BeforeEach
            void init() {
                String incomeSubCategoryName = "급여";
                final BookLineCategory bookLineCategory = BookLineCategoryFixture.incomeBookLineCategory(book, incomeSubCategoryName, assetSubCategoryName);
                bookLine = BookLineFixture.createWithDate(book, bookUser, bookLineCategory, LocalDate.now());
            }

            @Test
            @DisplayName("금액이 양수인, 이월 내역이 생성된다")
            void it_return_positive_money() {
                CarryOver carryOver = CarryOver.getCarryOverToAdd(bookLine, LocalDate.now().plusDays(1));
                Assertions.assertThat(carryOver.getMoney()).isEqualTo(bookLine.getMoney());
            }
        }

        @Nested
        @DisplayName("카테고리가 지출인 가계부 내역이 주어지면")
        class Context_With_OutcomeBookLine {

            BookLine bookLine;

            @BeforeEach
            void init() {
                String outcomeSubCategory = "급여";
                final BookLineCategory bookLineCategory = BookLineCategoryFixture.outcomeBookLineCategory(book, outcomeSubCategory, assetSubCategoryName);
                bookLine = BookLineFixture.createWithDate(book, bookUser, bookLineCategory, LocalDate.now());
            }

            @Test
            @DisplayName("금액이 음수인, 이월 내역이 생성된다")
            void it_return_negative_money() {
                CarryOver carryOver = CarryOver.getCarryOverToAdd(bookLine, LocalDate.now().plusDays(1));
                Assertions.assertThat(carryOver.getMoney()).isEqualTo(-1 * bookLine.getMoney());
            }

        }
    }

    @Nested
    @DisplayName("getCarryOverToDelete()를 실행할 때")
    class Describe_GetCarryOverToDelete {
        User user;
        Book book;
        String bookKey;
        BookUser bookUser;
        String assetSubCategoryName = "자산";

        @BeforeEach
        void init() {
            user = UserFixture.emailUser();
            book = BookFixture.createBook();
            bookKey = book.getBookKey();
            bookUser = BookUserFixture.createBookUser(book, user);
        }

        @Nested
        @DisplayName("수입 이월 내역이 주어지면")
        class Context_With_IncomeMoney {
            BookLine bookLine;
            CarryOver carryOver;

            @BeforeEach
            void init() {
                String incomeSubCategoryName = "급여";
                final BookLineCategory bookLineCategory = BookLineCategoryFixture.incomeBookLineCategory(book, incomeSubCategoryName, assetSubCategoryName);
                bookLine = BookLineFixture.createWithDate(book, bookUser, bookLineCategory, LocalDate.now());
                carryOver = CarryOver.getCarryOverToDelete(bookLine, LocalDate.now().plusDays(1));
            }

            @Test
            @DisplayName("이월 금액은 음수가 된다")
            void it_return_negative_money() {
                Assertions.assertThat(carryOver.getMoney()).isEqualTo(-1 * bookLine.getMoney());
            }
        }

        @Nested
        @DisplayName("지출 이월 내역이 주어지면")
        class Context_With_OutcomeMoney {

            CarryOver carryOver;
            BookLine bookLine;

            @BeforeEach
            void init() {
                String outcomeSubCategory = "식비";
                final BookLineCategory bookLineCategory = BookLineCategoryFixture.outcomeBookLineCategory(book, outcomeSubCategory, assetSubCategoryName);
                bookLine = BookLineFixture.createWithDate(book, bookUser, bookLineCategory, LocalDate.now());
                carryOver = CarryOver.getCarryOverToDelete(bookLine, LocalDate.now().plusDays(1));
            }

            @Test
            @DisplayName("이월 금액은 양수가 된다")
            void it_return_positive_money() {
                Assertions.assertThat(carryOver.getMoney()).isEqualTo(bookLine.getMoney());
            }
        }
    }

    @Nested
    @DisplayName("delete()를 실행할 때")
    class Describe_Delete {
        User user;
        Book book;
        String bookKey;
        BookUser bookUser;
        String assetSubCategoryName = "자산";

        @BeforeEach
        void init() {
            user = UserFixture.emailUser();
            book = BookFixture.createBook();
            bookKey = book.getBookKey();
            bookUser = BookUserFixture.createBookUser(book, user);
        }

        @Nested
        @DisplayName("수입 이월 내역이 주어지면")
        class Context_With_IncomeMoney {
            CarryOver carryOver;
            BookLine bookLine;
            BookLineCategory bookLineCategory;

            @BeforeEach
            void init() {
                String incomeSubCategoryName = "급여";
                bookLineCategory = BookLineCategoryFixture.incomeBookLineCategory(book, incomeSubCategoryName, assetSubCategoryName);
                bookLine = BookLineFixture.createWithDate(book, bookUser, bookLineCategory, LocalDate.now());
                carryOver = CarryOver.getCarryOverToDelete(bookLine, LocalDate.now().plusDays(1));
            }

            @Test
            @DisplayName("이월 금액은 음수가 된다")
            void it_return_negative_money() {
                Assertions.assertThat(carryOver.getMoney()).isEqualTo(-1 * bookLine.getMoney());
            }
        }

        @Nested
        @DisplayName("지출 이월 내역이 주어지면")
        class Context_With_OutcomeMoney {
            CarryOver carryOver;
            BookLineCategory bookLineCategory;
            BookLine bookLine;

            @BeforeEach
            void init() {
                String outcomeSubCategory = "식비";
                bookLineCategory = BookLineCategoryFixture.outcomeBookLineCategory(book, outcomeSubCategory, assetSubCategoryName);
                bookLine = BookLineFixture.createWithDate(book, bookUser, bookLineCategory, LocalDate.now());
                carryOver = CarryOver.getCarryOverToDelete(bookLine, LocalDate.now().plusDays(1));
            }

            @Test
            @DisplayName("이월 금액은 양수가 된다")
            void it_return_positive_money() {
                Assertions.assertThat(carryOver.getMoney()).isEqualTo(bookLine.getMoney());
            }
        }

        @Nested
        @DisplayName("이체 이월 내역이 주어지면")
        class Context_With_TransferMoney {
            CarryOver carryOver;
            BookLineCategory bookLineCategory;
            BookLine bookLine;

            @BeforeEach
            void init() {
                String transferSubCategory = "이체";
                bookLineCategory = BookLineCategoryFixture.transferBookLineCategory(book, transferSubCategory, assetSubCategoryName);
                bookLine = BookLineFixture.createWithDate(book, bookUser, bookLineCategory, LocalDate.now());
                carryOver = CarryOver.getCarryOverToDelete(bookLine, LocalDate.now().plusDays(1));
            }

            @Test
            @DisplayName("이월 금액은 변하지 않는다")
            void it_maintain_money() {
                Assertions.assertThat(carryOver.getMoney()).isEqualTo(bookLine.getMoney());
            }
        }
    }
}
