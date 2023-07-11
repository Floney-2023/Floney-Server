package com.floney.floney.book;

import com.floney.floney.book.dto.CarryOverInfo;
import com.floney.floney.book.entity.CarryOver;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.floney.floney.book.dto.constant.AssetType.INCOME;
import static com.floney.floney.book.dto.constant.AssetType.OUTCOME;

public class CarryOverTest {

    @Test
    @DisplayName("이월은 총수입-총지출을 계산하여 총 지출이 클 시 지출내역이 된다")
    void calculate_outcome() {
        CarryOver newCarryOver = CarryOver.builder()
            .income(1000L)
            .outcome(2000L)
            .book(BookFixture.createBook())
            .build();

        CarryOverInfo carryOverInfo = newCarryOver.calculateValue();
        Assertions.assertThat(carryOverInfo.getAssetType()).isEqualTo(OUTCOME);
    }

    @Test
    @DisplayName("이월은 총수입-총지출을 계산하여 총수입이 클 시 수입내역이 된다")
    void calculate_income() {
        CarryOver newCarryOver = CarryOver.builder()
            .income(3000L)
            .outcome(2000L)
            .book(BookFixture.createBook())
            .build();

        CarryOverInfo carryOverInfo = newCarryOver.calculateValue();
        Assertions.assertThat(carryOverInfo.getAssetType()).isEqualTo(INCOME);
    }
}
