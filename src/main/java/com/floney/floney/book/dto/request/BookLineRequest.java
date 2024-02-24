package com.floney.floney.book.dto.request;

import com.floney.floney.book.domain.RepeatDuration;
import com.floney.floney.book.domain.entity.BookLine;
import com.floney.floney.book.domain.entity.BookLineCategory;
import com.floney.floney.book.domain.entity.BookUser;
import lombok.*;

import java.time.LocalDate;

@Getter
@ToString
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookLineRequest {

    private long lineId;
    private String bookKey;
    private double money;
    private String flow;
    private String asset;
    private String line;
    private LocalDate lineDate;
    private String description;
    private Boolean except;
    private RepeatDuration repeatDuration;

    public BookLine to(final BookUser bookUser, final BookLineCategory bookLineCategory) {
        return BookLine.builder()
            .book(bookUser.getBook())
            .writer(bookUser)
            .lineDate(lineDate)
            .money(money)
            .description(description)
            .exceptStatus(except)
            .categories(bookLineCategory)
            .build();
    }
}
