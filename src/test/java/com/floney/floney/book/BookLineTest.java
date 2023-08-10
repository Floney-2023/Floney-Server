package com.floney.floney.book;

import com.floney.floney.book.dto.process.DayLineByDayView;
import com.floney.floney.book.dto.process.DayLines;
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
    @DisplayName("아이디가 동일한 내역은 중복된 내용은 합치고, 카테고리는 리스트 형태로 반환한다")
    void format_response() {
        DayLineByDayView dayLine = DayLineByDayView.builder()
            .id(1L)
            .categories("수입")
            .content("내용")
            .money(1000L)
            .profileImg("img")
            .build();

        DayLineByDayView dayLine2 = DayLineByDayView.builder()
            .id(1L)
            .categories("급여")
            .content("내용")
            .money(1000L)
            .profileImg("img")
            .build();

        List<DayLines> result = Arrays.asList(DayLines.builder()
            .assetType(AssetType.find("수입"))
            .img("img")
            .category(Arrays.asList("급여"))
            .money(1000L)
            .content("내용")
            .build());


        Assertions.assertThat(DayLines.forDayView(Arrays.asList(dayLine, dayLine2))).isEqualTo(result);
    }

}
