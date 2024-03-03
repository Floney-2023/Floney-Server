package com.floney.floney.book.domain.entity;

import com.floney.floney.acceptance.config.AcceptanceTest;
import com.floney.floney.book.domain.RepeatDuration;
import com.floney.floney.fixture.*;
import com.floney.floney.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.floney.floney.book.domain.entity.RepeatBookLine.REPEAT_YEAR;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@AcceptanceTest
@DisplayName("단위 테스트: 반복 내역")
public class RepeatBookLineTest {

    private void assertBookLineSameWithRepeat(List<BookLine> bookLines, RepeatBookLine repeatBookLine) {
        BookLine targetBookLineToAssert = bookLines.get(0);

        assertAll(() -> assertThat(targetBookLineToAssert.getBook()).isEqualTo(repeatBookLine.getBook()),
            () -> assertThat(targetBookLineToAssert.getMoney()).isEqualTo(repeatBookLine.getMoney()),
            () -> assertThat(targetBookLineToAssert.getWriter()).isEqualTo(repeatBookLine.getWriter()),
            () -> assertThat(targetBookLineToAssert.getCategories().getLineCategory()).isEqualTo(repeatBookLine.getLineCategory()),
            () -> assertThat(targetBookLineToAssert.getCategories().getAssetSubcategory()).isEqualTo(repeatBookLine.getAssetSubcategory()),
            () -> assertThat(targetBookLineToAssert.getExceptStatus()).isEqualTo(repeatBookLine.getExceptStatus()));
    }


    @Nested
    @DisplayName("반복 내역을 생성할 때")
    class Describe_Of {

        Book book;

        BookUser bookUser;

        @BeforeEach
        void init() {
            User user = UserFixture.emailUser();
            book = BookFixture.createBook();
            bookUser = BookUserFixture.createBookUser(book, user);
        }

        @Nested
        @DisplayName("가계부 내역, 반복 주기, 가계부가 주어진 경우")
        class Context_With_LineBookRepeatDuration {
            BookLine bookLine;

            @BeforeEach
            public void init() {
                BookLineCategory bookLineCategory = BookLineCategoryFixture.incomeBookLineCategory(book, "급여", "현금");
                bookLine = BookLineFixture.createWithDate(book, bookUser, bookLineCategory, LocalDate.now());
            }

            @Test
            @DisplayName("생성된다.")
            void it_returns_repeatBookLine() {
                RepeatBookLine repeatBookLine = RepeatBookLine.of(bookLine, RepeatDuration.WEEKEND);
                assertThat(repeatBookLine).isNotNull();
            }

            @Test
            @DisplayName("생성된 반복 내역은 가계부 내역과 일치한다")
            void it_same_with_bookLine() {
                RepeatBookLine repeatBookLine = RepeatBookLine.of(bookLine, RepeatDuration.WEEKEND);
                assertAll(() -> assertThat(repeatBookLine.getBook()).isEqualTo(bookLine.getBook()),
                    () -> assertThat(repeatBookLine.getMoney()).isEqualTo(bookLine.getMoney()),
                    () -> assertThat(repeatBookLine.getWriter()).isEqualTo(bookLine.getWriter()),
                    () -> assertThat(repeatBookLine.getLineCategory()).isEqualTo(bookLine.getCategories().getLineCategory()),
                    () -> assertThat(repeatBookLine.getAssetSubcategory()).isEqualTo(bookLine.getCategories().getAssetSubcategory()),
                    () -> assertThat(repeatBookLine.getDescription()).isEqualTo(bookLine.getDescription()),
                    () -> assertThat(repeatBookLine.getExceptStatus()).isEqualTo(bookLine.getExceptStatus()));

            }
        }
    }

    @Nested
    @DisplayName("반복 내역으로 가계부 내역을 생성할 때")
    class Describe_BookLinesByRepeat {
        BookLine bookLine;
        Book book;
        RepeatBookLine repeatBookLine;
        LocalDate lineDate = LocalDate.now();

        @BeforeEach
        public void init() {
            User user = UserFixture.emailUser();
            book = BookFixture.createBook();
            BookUser bookUser = BookUserFixture.createBookUser(book, user);
            BookLineCategory bookLineCategory = BookLineCategoryFixture.incomeBookLineCategory(book, "급여", "은행");

            bookLine = BookLineFixture.createWithDate(book, bookUser, bookLineCategory, lineDate);
            repeatBookLine = RepeatBookLine.of(bookLine, RepeatDuration.WEEKEND);
        }

        @Nested
        @DisplayName("매일을 선택한 경우")
        class Context_With_EveryDay {

            List<BookLine> bookLines;

            @BeforeEach
            public void init() {
                bookLines = repeatBookLine.bookLinesBy(RepeatDuration.EVERYDAY);
            }

            @Test
            @DisplayName("매일 가계부 내역이 생성된다")
            void it_returns_bookLines() {
                LocalDate startDate = lineDate.plusDays(1);
                LocalDate endDate = lineDate.plusYears(REPEAT_YEAR);
                long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;

                assertBookLineSameWithRepeat(bookLines, repeatBookLine);
                assertThat(bookLines.size()).isEqualTo(daysBetween);
                assertThat(bookLines.get(0).getLineDate()).isEqualTo(lineDate.plusDays(1));
            }
        }

        @Nested
        @DisplayName("매주를 선택한 경우")
        class Context_With_EveryWeek {

            List<BookLine> bookLines;

            @BeforeEach
            public void init() {
                bookLines = repeatBookLine.bookLinesBy(RepeatDuration.WEEK);
            }

            @Test
            @DisplayName("매주 같은 요일에 가계부 내역이 생성된다")
            void it_returns_bookLines() {
                LocalDate startDate = lineDate.plusDays(1);
                LocalDate endDate = lineDate.plusYears(REPEAT_YEAR);
                long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;

                assertBookLineSameWithRepeat(bookLines, repeatBookLine);
                assertThat(bookLines.size()).isEqualTo(daysBetween / 7);
                assertThat(bookLines.get(0).getLineDate()).isEqualTo(lineDate.plusDays(7));

            }
        }

        @Nested
        @DisplayName("매달을 선택한 경우")
        class Context_With_EveryMonth {

            List<BookLine> bookLines;

            @BeforeEach
            public void init() {
                bookLines = repeatBookLine.bookLinesBy(RepeatDuration.MONTH);
            }

            @Test
            @DisplayName("매달 같은 일자에 가계부 내역이 생성된다")
            void it_returns_bookLines() {
                assertBookLineSameWithRepeat(bookLines, repeatBookLine);
                assertThat(bookLines.get(0).getLineDate()).isEqualTo(lineDate.plusMonths(1));
            }
        }

        @Nested
        @DisplayName("주중을 선택한 경우")
        class Context_With_EveryWeekDay {

            List<BookLine> bookLines;

            @BeforeEach
            public void init() {
                bookLines = repeatBookLine.bookLinesBy(RepeatDuration.WEEKDAY);

            }

            @Test
            @DisplayName("주중 일자에 가계부 내역이 생성된다")
            void it_returns_bookLines() {
                LocalDate endDate = lineDate.plusYears(REPEAT_YEAR);
                long weekdayCount = 0;

                for (LocalDate date = lineDate.plusDays(1); date.isBefore(endDate) || date.isEqual(endDate); date = date.plusDays(1)) {
                    if (date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY) {
                        weekdayCount++;
                    }
                }

                assertBookLineSameWithRepeat(bookLines, repeatBookLine);
                assertThat(bookLines.size()).isEqualTo(weekdayCount);

            }
        }

        @Nested
        @DisplayName("주말을 선택한 경우")
        class Context_With_EveryWeekend {

            List<BookLine> bookLines;

            @BeforeEach
            public void init() {
                bookLines = repeatBookLine.bookLinesBy(RepeatDuration.WEEKEND);
            }

            @Test
            @DisplayName("주말 일자에 가계부 내역이 생성된다")
            void it_returns_bookLines() {
                LocalDate startDate = lineDate.plusDays(1);
                LocalDate endDate = lineDate.plusYears(REPEAT_YEAR);

                long weekendCount = 0;

                for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
                    if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                        weekendCount++;
                    }
                }

                assertBookLineSameWithRepeat(bookLines, repeatBookLine);
                assertThat(bookLines.size()).isEqualTo(weekendCount);
            }
        }
    }
}

