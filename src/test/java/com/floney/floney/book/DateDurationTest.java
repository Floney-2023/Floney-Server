package com.floney.floney.book;

import com.floney.floney.common.domain.vo.DateDuration;
import com.floney.floney.common.exception.book.LimitRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.floney.floney.book.domain.constant.ExcelDuration.*;
import static com.floney.floney.common.domain.vo.DateDuration.currentToAfterMonth;
import static com.floney.floney.common.domain.vo.DateDuration.durationByExcelDuration;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DisplayName("DateDuration 테스트")
public class DateDurationTest {

    @Test
    @DisplayName("현재 날짜가 속한 해당 월의 첫날을 startDate로, 마지막날을 endDate로 기간을 반환한다")
    void getStartAndEndOfMonth() {
        DateDuration datesDuration = DateDuration.startAndEndOfMonth("2023-05-01");

        assertThat(datesDuration.getStartDate())
            .isEqualTo("2023-05-01");
        assertThat(datesDuration.getEndDate())
            .isEqualTo("2023-05-31");
    }

    @Test
    @DisplayName("해당 날짜가 속한 년도의 첫날을 startDate로, 마지막날을 endDate로 기간을 반환한다")
    void getFirstAndEndDayOfYear() {
        LocalDate currentDate = LocalDate.of(2023, 10, 1);
        DateDuration duration = DateDuration.firstAndEndDayOfYear(currentDate);

        assertThat(duration.getStartDate())
            .isEqualTo(LocalDate.of(2023, 1, 1));
        assertThat(duration.getEndDate())
            .isEqualTo(LocalDate.of(2023, 12, 31));
    }

    @Test
    @DisplayName("현재 날짜로부터 N개월 전을 startDate로, 현재 날짜를 endDate로 기간을 반환한다")
    void getAssetDuration() {
        LocalDate currentDate = LocalDate.of(2023, 10, 1);
        DateDuration duration = DateDuration.beforeMonthToCurrent(currentDate, 5);

        assertThat(duration.getStartDate())
            .isEqualTo(LocalDate.of(2023, 5, 1));
        assertThat(duration.getEndDate())
            .isEqualTo(currentDate);
    }

    @Test
    @DisplayName("현재 날짜로부터 이전달의 시작과 마지막날의 기간을 반환한다")
    void getLastMonthDateDuration() {
        LocalDate currentDate = LocalDate.of(2023, 10, 1);
        DateDuration duration = DateDuration.firstAndLastDayFromLastMonth(currentDate);

        assertThat(duration.getStartDate())
            .isEqualTo(LocalDate.of(2023, 9, 1));
        assertThat(duration.getEndDate())
            .isEqualTo(LocalDate.of(2023, 9, 30));
    }

    @Test
    @DisplayName("현시점을 startDate로, 특정 개월 이후를 endDate로 기간을 반환한다")
    void afterMonth() {
        LocalDate firstDayOfMonth = LocalDate.of(2024, 1, 1);
        assertThat(currentToAfterMonth(firstDayOfMonth, 3).getEndDate())
            .isEqualTo(LocalDate.of(2024, 3, 31));
    }

    @Nested
    @DisplayName("getDurationByExcelDuration 메서드는 ")
    class ExcelDate {
        @Test
        @DisplayName("이번달을 ExcelDuration으로 넣을 경우, 1일과 마지막 날을 기간으로 반환한다")
        void thisMonth() {
            String currentDate = "2024-01-01";
            DateDuration duration = durationByExcelDuration(currentDate, THIS_MONTH);
            assertThat(duration.getStartDate()).isEqualTo(LocalDate.of(2024, 1, 1));
            assertThat(duration.getEndDate()).isEqualTo(LocalDate.of(2024, 1, 31));
        }

        @Test
        @DisplayName("지난달을 ExcelDuration으로 넣을 경우, 1일과 마지막날을 기간으로 반환한다")
        void lastMonth() {
            String currentDate = "2024-01-01";
            DateDuration duration = durationByExcelDuration(currentDate, LAST_MONTH);
            assertThat(duration.getStartDate()).isEqualTo(LocalDate.of(2023, 12, 1));
            assertThat(duration.getEndDate()).isEqualTo(LocalDate.of(2023, 12, 31));
        }

        @Test
        @DisplayName("1년을 ExcelDuration으로 넣을 경우, 현 시점으로 부터, 1년의 기간을 반환한다")
        void afterOneYear() {
            String currentDate = "2024-01-01";
            DateDuration duration = durationByExcelDuration(currentDate, ONE_YEAR);
            assertThat(duration.getStartDate()).isEqualTo(LocalDate.of(2024, 1, 1));
            assertThat(duration.getEndDate()).isEqualTo(LocalDate.of(2024, 12, 31));
        }

        @Test
        @DisplayName("이번달,저번달,1년 그 외의 값을 넣을 경우, 예외를 반환한다")
        void occurError() {
            String currentDate = "2024-01-01";
            assertThatThrownBy(() -> durationByExcelDuration(currentDate, ALL))
                .isInstanceOf(LimitRequestException.class);
        }
    }

}
