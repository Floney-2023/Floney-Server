package com.floney.floney.book;

import com.floney.floney.book.util.DateFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class DateFactoryTest {

    @Test
    @DisplayName("현재 날짜로, 해당 월의 첫날과 마지막날을 반환한다")
    void getFirstDayAndEndOfMonth() {
        assertThat(DateFactory.getStartAndEndOfMonth("2023-05-01").start())
            .isEqualTo("2023-05-01");
        assertThat(DateFactory.getStartAndEndOfMonth("2023-05-01").end())
            .isEqualTo("2023-05-31");
    }

    @Test
    @DisplayName("해당 기간의 월별 지출/수입 초기화 객체를 만들어준다 : { 월 : { 지출  : 0 , 수입 : 0 } } ")
    void getInitExpenseByMonth() {
        Assertions.assertThat(DateFactory.getInitBookLineExpenseByMonth("2023-05-01")
                .size())
            .isEqualTo(62);
    }
}
