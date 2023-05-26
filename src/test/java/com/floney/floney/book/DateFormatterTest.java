package com.floney.floney.book;

import com.floney.floney.book.util.DateFormatter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class DateFormatterTest {

    @Test
    @DisplayName("YYYY-MM이 들어오면 해당 월의 첫날과 마지막날을 반환한다")
    void dates(){
        assertThat(DateFormatter.getDate("2023-05-01").get("start")).isEqualTo("2023-05-01");
        assertThat(DateFormatter.getDate("2023-05-01").get("end")).isEqualTo("2023-05-31");
    }

    @Test
    @DisplayName("해당 달에 몇일까지 존재하는지 반환한다")
    void month_length(){
        assertThat(DateFormatter.countDays("2023-05-01")).isEqualTo(31);
    }
}
