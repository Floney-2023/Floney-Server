package com.floney.floney.book.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.floney.floney.book.util.DateUtil.isFirstDay;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class DateUtilTest {

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
        assertThat(DateUtil.getFirstDayOfMonth(currentDate))
            .isEqualTo(LocalDate.of(2024, 1, 1));
    }

    @Test
    @DisplayName("현재로부터 N달 이전의 날짜를 반환한다")
    void getDateBeforeMonth() {
        LocalDate currentDate = LocalDate.of(2024, 12, 1);
        assertThat(DateUtil.getDateBeforeMonth(currentDate, 2))
            .isEqualTo(LocalDate.of(2024, 10, 1));
    }

    @Test
    @DisplayName("현 날짜가 속한 달의 마지막날을 구한다")
    void getLastDateOfMonth() {
        LocalDate currentDate = LocalDate.of(2024, 12, 1);
        assertThat(DateUtil.getLastDateOfMonth(currentDate))
            .isEqualTo(LocalDate.of(2024, 12, 31));
    }

}
