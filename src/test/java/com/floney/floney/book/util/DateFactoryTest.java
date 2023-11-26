package com.floney.floney.book.util;

import com.floney.floney.book.util.DateFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class DateFactoryTest {

    @Test
    @DisplayName("YYYY-MM이 들어오면 해당 월의 첫날과 마지막날을 반환한다")
    void dates() {
        assertThat(DateFactory.getDateDuration("2023-05-01").start())
                .isEqualTo("2023-05-01");
        assertThat(DateFactory.getDateDuration("2023-05-01").end())
                .isEqualTo("2023-05-31");
    }


    @Test
    @DisplayName("월의 일수만큼 초기화된 총 수입,총 지출을 담은 리스트를 반환한다")
    void date_storage() {
        Assertions.assertThat(DateFactory.initDates("2023-05-01")
                        .size())
                .isEqualTo(62);
    }

    @Test
    @DisplayName("해당 월의 지난 달을 구한다")
    void before_month() {
        Assertions.assertThat(DateFactory.getBeforeMonth(LocalDate.of(2023, 5, 1))).isEqualTo("2023-04-01");
    }
}
