package com.floney.floney.book;

import com.floney.floney.book.dto.process.DatesDuration;
import com.floney.floney.book.util.DateFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.floney.floney.book.util.DateFactory.getInitBookLineExpenseByMonth;
import static com.floney.floney.book.util.DateFactory.isFirstDay;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class DateFactoryTest {

    @Test
    @DisplayName("현재 날짜로, 해당 월의 첫날과 마지막날을 반환한다")
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
    @DisplayName("해당 날짜가 속한 년도의 첫날과 마지막날을 반환한다")
    void getFirstAndEndDayOfYear() {
        LocalDate currentDate = LocalDate.of(2023, 10, 1);
        DatesDuration duration = DateFactory.getFirstAndEndDayOfYear(currentDate);

        assertThat(duration.start())
            .isEqualTo(LocalDate.of(2023, 1, 1));
        assertThat(duration.end())
            .isEqualTo(LocalDate.of(2023, 12, 31));
    }

    @Test
    @DisplayName("현재 날짜로부터 5개월 이전(자산 분석 기간)을 시작으로 기간을 반환한다")
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

}
