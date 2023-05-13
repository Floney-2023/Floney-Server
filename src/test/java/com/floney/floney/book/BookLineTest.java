package com.floney.floney.book;

import com.floney.floney.book.dto.DayLine;
import com.floney.floney.book.dto.DayLinesResponse;
import com.floney.floney.book.dto.constant.AssetType;
import com.floney.floney.book.entity.Book;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static com.floney.floney.book.BookFixture.BOOK_KEY;

public class BookLineTest {

    @Test
    @DisplayName("지출 내역을 예산에서 포함 시킬 시 예산에서 해당 값을 뺀다")
    void outcome() {
        Long outcome = 1000L;
        Long initialAsset = 2000L;
        Book book = Book.builder()
            .bookKey(BOOK_KEY)
            .initialAsset(initialAsset)
            .build();

        book.processTrans(BookLineFixture.createOutcomeRequest());
        Assertions.assertThat(book.getInitialAsset())
            .isEqualTo(initialAsset - outcome);
    }

    @Test
    @DisplayName("수입 내역을 자산에서 포함 시킬 시 해당 값을 더한다")
    void income() {
        Long income = 1000L;
        Long budget = 2000L;
        Book book = Book.builder()
            .bookKey(BOOK_KEY)
            .budget(budget)
            .build();

        book.processTrans(BookLineFixture.createIncomeRequest());
        Assertions.assertThat(book.getBudget())
            .isEqualTo(budget + income);
    }

    @Test
    @DisplayName("아이디가 동일한 내역은 중복된 내용은 합치고, 카테고리는 리스트 형태로 반환한다")
    void format_response() {
        DayLine dayLine = DayLine.builder()
            .id(1L)
            .categories("수입")
            .content("내용")
            .money(1000L)
            .profileImg("img")
            .build();

        DayLine dayLine2 = DayLine.builder()
            .id(1L)
            .categories("급여")
            .content("내용")
            .money(1000L)
            .profileImg("img")
            .build();

        List<DayLinesResponse> result = Arrays.asList(DayLinesResponse.builder()
            .assetType(AssetType.find("수입"))
            .img("img")
            .category(Arrays.asList("급여"))
            .money(1000L)
            .content("내용")
            .build());


        Assertions.assertThat(DayLinesResponse.of(Arrays.asList(dayLine, dayLine2))).isEqualTo(result);
    }

}
