package com.floney.floney.book;

import com.floney.floney.book.dto.constant.DayType;
import com.floney.floney.book.dto.process.DatesDuration;
import com.floney.floney.book.util.DateFactory;
import com.floney.floney.common.exception.book.LimitRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.floney.floney.book.dto.constant.ExcelDuration.*;
import static com.floney.floney.book.util.DateFactory.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DisplayName("DateFactory 테스트")
public class DateFactoryTest {

    @Test
    @DisplayName("현재 날짜가 속한 해당 월의 첫날을 startDate로, 마지막날을 endDate로 기간을 반환한다")
    void getStartAndEndOfMonth() {
        DatesDuration datesDuration = DateFactory.getStartAndEndOfMonth("2023-05-01");

        assertThat(datesDuration.start())
                .isEqualTo("2023-05-01");
        assertThat(datesDuration.end())
                .isEqualTo("2023-05-31");
    }

    @Test
    @DisplayName("해당 월의 모든 일별로 지출,수입을 Key로 초기화하는 객체를 반한한다")
    void getInitExpenseByMonth() {
        assertThat(getInitBookLineExpenseByMonth("2023-05-01")
                .size())
                .isEqualTo(62);
    }

    @Test
    @DisplayName("해당 날짜가 속한 년도의 첫날을 startDate로, 마지막날을 endDate로 기간을 반환한다")
    void getFirstAndEndDayOfYear() {
        LocalDate currentDate = LocalDate.of(2023, 10, 1);
        DatesDuration duration = DateFactory.getFirstAndEndDayOfYear(currentDate);

        assertThat(duration.start())
                .isEqualTo(LocalDate.of(2023, 1, 1));
        assertThat(duration.end())
                .isEqualTo(LocalDate.of(2023, 12, 31));
    }

    @Test
    @DisplayName("현재 날짜로부터 5개월 이전(자산 분석 기간)을 startDate로, 현재 날짜를 endDate로 기간을 반환한다")
    void getAssetDuration() {
        LocalDate currentDate = LocalDate.of(2023, 10, 1);
        DatesDuration duration = DateFactory.getAssetDuration(currentDate);

        assertThat(duration.start())
                .isEqualTo(LocalDate.of(2023, 5, 1));
        assertThat(duration.end())
                .isEqualTo(currentDate);
    }

    @Test
    @DisplayName("현재 날짜로부터 이전달의 시작과 마지막날의 기간을 반환한다")
    void getLastMonthDateDuration() {
        LocalDate currentDate = LocalDate.of(2023, 10, 1);
        DatesDuration duration = DateFactory.getLastMonthDateDuration(currentDate);

        assertThat(duration.start())
                .isEqualTo(LocalDate.of(2023, 9, 1));
        assertThat(duration.end())
                .isEqualTo(LocalDate.of(2023, 9, 30));
    }

    @Test
    @DisplayName("현재 날짜가 월의 첫날인지 반환한다")
    void isFirstDate() {
        String firstDate = "2023-05-01";
        String notFirstDate = "2023-05-13";

        assertThat(isFirstDay(firstDate)).isTrue();
        assertThat(isFirstDay(notFirstDate)).isFalse();
    }

    @Test
    @DisplayName("현재 월의 첫날을 반환하다")
    void getFirstDayOfMonth() {
        LocalDate currentDate = LocalDate.of(2024, 1, 20);
        assertThat(DateFactory.getFirstDayOfMonth(currentDate))
                .isEqualTo(LocalDate.of(2024, 1, 1));
    }

    @Test
    @DisplayName("현시점을 startDate로, 특정 개월 이후를 endDate로 기간을 반환한다")
    void afterMonth() {
        LocalDate firstDayOfMonth = LocalDate.of(2024, 1, 1);
        assertThat(getAfterMonthDuration(firstDayOfMonth, DayType.THREE_MONTH).end())
                .isEqualTo(LocalDate.of(2024, 3, 31));
    }

    @Nested
    @DisplayName("getDurationByExcelDuration 메서드는 ")
    class ExcelDate {
        @Test
        @DisplayName("이번달을 ExcelDuration으로 넣을 경우, 1일과 마지막 날을 기간으로 반환한다")
        void thisMonth() {
            String currentDate = "2024-01-01";
            DatesDuration duration = getDurationByExcelDuration(currentDate, THIS_MONTH);
            assertThat(duration.getStartDate()).isEqualTo(LocalDate.of(2024, 1, 1));
            assertThat(duration.getEndDate()).isEqualTo(LocalDate.of(2024, 1, 31));
        }

        @Test
        @DisplayName("지난달을 ExcelDuration으로 넣을 경우, 1일과 마지막날을 기간으로 반환한다")
        void lastMonth() {
            String currentDate = "2024-01-01";
            DatesDuration duration = getDurationByExcelDuration(currentDate, LAST_MONTH);
            assertThat(duration.getStartDate()).isEqualTo(LocalDate.of(2023, 12, 1));
            assertThat(duration.getEndDate()).isEqualTo(LocalDate.of(2023, 12, 31));
        }

        @Test
        @DisplayName("1년을 ExcelDuration으로 넣을 경우, 현 시점으로 부터, 1년의 기간을 반환한다")
        void afterOneYear() {
            String currentDate = "2024-01-01";
            DatesDuration duration = getDurationByExcelDuration(currentDate, ONE_YEAR);
            assertThat(duration.getStartDate()).isEqualTo(LocalDate.of(2024, 1, 1));
            assertThat(duration.getEndDate()).isEqualTo(LocalDate.of(2024, 12, 31));
        }

        @Test
        @DisplayName("이번달,저번달,1년 그 외의 값을 넣을 경우, 예외를 반환한다")
        void occurError() {
            String currentDate = "2024-01-01";
            assertThatThrownBy(() -> getDurationByExcelDuration(currentDate, ALL))
                    .isInstanceOf(LimitRequestException.class);
        }
    }

}
