package com.floney.floney.book.dto.response;

import com.floney.floney.book.domain.entity.BookLine;
import lombok.*;

import java.time.LocalDate;

@Getter
@ToString
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BookLineResponse {

    private final double money;
    private final String flow;
    private final String asset;
    private final String line;
    private final LocalDate lineDate;
    private final String description;
    private final Boolean except;
    private final String nickname;

    public static BookLineResponse from(final BookLine bookLine) {
        return BookLineResponse.builder()
            .money(bookLine.getMoney())
            .flow(bookLine.getCategories().getLineCategory().getName().getMeaning())
            .asset(bookLine.getCategories().getAssetSubCategory().getName())
            .line(bookLine.getCategories().getLineSubCategory().getName())
            .lineDate(bookLine.getLineDate())
            .description(bookLine.getDescription())
            .except(bookLine.getExceptStatus())
            .nickname(bookLine.getWriter())
            .build();
    }
}
