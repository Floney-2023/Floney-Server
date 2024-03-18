package com.floney.floney.book.dto.response;

import com.floney.floney.book.domain.RepeatDuration;
import com.floney.floney.book.domain.entity.BookLine;
import lombok.*;

import java.time.LocalDate;

@Getter
@ToString
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BookLineResponse {

    private final long id;
    private final double money;
    private final String flow;
    private final String asset;
    private final String line;
    private final LocalDate lineDate;
    private final String description;
    private final Boolean except;
    private final String nickname;
    private final RepeatDuration repeatDuration;

    public static BookLineResponse from(final BookLine bookLine) {
        return BookLineResponse.builder()
            .id(bookLine.getId())
            .money(bookLine.getMoney())
            .flow(bookLine.getCategories().getLineCategory().getName().getMeaning())
            .asset(bookLine.getCategories().getAssetSubcategory().getName())
            .line(bookLine.getCategories().getLineSubcategory().getName())
            .lineDate(bookLine.getLineDate())
            .description(bookLine.getDescription())
            .except(bookLine.getExceptStatus())
            .nickname(bookLine.getWriterNickName())
            .repeatDuration(bookLine.getRepeatDuration())
            .build();
    }
}
