package com.floney.floney.fixture;

import com.floney.floney.book.domain.RepeatDuration;
import com.floney.floney.book.dto.request.BookLineRequest;

import java.time.LocalDate;

public class BookRequestDtoFixture {

    public static BookLineRequest createBookLineRequest(String bookKey, String lineCategory, String subCategory, String assetSubCategory) {
        return BookLineRequest.builder()
            .money(1000)
            .line(subCategory)
            .bookKey(bookKey)
            .flow(lineCategory)
            .asset(assetSubCategory)
            .lineDate(LocalDate.of(2024, 2, 8))
            .description("예시")
            .except(false)
            .repeatDuration(RepeatDuration.NONE)
            .build();
    }

    public static BookLineRequest createBookLineRequest(LocalDate lineDate, String bookKey, String lineCategory, String subCategory, String assetSubCategory, RepeatDuration repeatDuration) {
        return BookLineRequest.builder()
            .money(1000)
            .line(subCategory)
            .bookKey(bookKey)
            .flow(lineCategory)
            .asset(assetSubCategory)
            .lineDate(lineDate)
            .description("예시")
            .except(false)
            .repeatDuration(repeatDuration)
            .build();
    }

}
