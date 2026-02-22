package com.floney.floney.analyze.dto.response;

import com.floney.floney.book.domain.entity.BookLine;
import lombok.*;

import java.time.LocalDate;

@Getter
@ToString
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BookLineResponse {
    private final double money;
    private final String asset;
    private final LocalDate lineDate;
    private final String description;
    private final String userProfileImg;

    public static BookLineResponse from(final BookLine bookLine) {
        return BookLineResponse.builder()
            .money(bookLine.getMoney())
            .asset(bookLine.getCategories().getAssetSubcategory().getName())
            .lineDate(bookLine.getLineDate())
            .description(bookLine.getDescription())
            .userProfileImg(bookLine.getWriter().getProfileImg())
            .build();
    }
}
