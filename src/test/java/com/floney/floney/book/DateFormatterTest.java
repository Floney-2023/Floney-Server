package com.floney.floney.book;

import com.floney.floney.book.util.DateFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class DateFormatterTest {

    @Test
    @DisplayName("YYYY-MM이 들어오면 해당 월의 첫날과 마지막날을 반환한다")
    void dates(){
        assertThat(DateFactory.getDate("2023-05-01").get("start")).isEqualTo("2023-05-01");
        assertThat(DateFactory.getDate("2023-05-01").get("end")).isEqualTo("2023-05-31");
    }


    @Test
    @DisplayName("날짜를 담은 리스트를 반환한다")
    void date_storage(){
        Assertions.assertThat(DateFactory.generateDatStorage("2023-05-01")
                .toString())
            .isEqualTo(31);
    }
}
