package com.floney.floney.book.dto.request;

import com.floney.floney.book.domain.RepeatDuration;
import com.floney.floney.book.domain.entity.BookLine;
import com.floney.floney.book.domain.entity.BookLineCategory;
import com.floney.floney.book.domain.entity.BookLineImg;
import com.floney.floney.book.domain.entity.BookUser;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@ToString
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookLineRequest {

    private long lineId;
    private String bookKey;
    private double money;
    private String flow; // TODO: 차후에 변수명 lineType으로 변경
    private String asset;
    private String line; // TODO: 차후에 변수명 subType으로 변경
    private LocalDate lineDate;
    private String description;
    private Boolean except;
    private RepeatDuration repeatDuration;
    private String memo;
    private List<String> imageUrl;

    public BookLine to(final BookUser bookUser, final BookLineCategory bookLineCategory) {
        return BookLine.builder()
            .book(bookUser.getBook())
            .writer(bookUser)
            .lineDate(lineDate)
            .money(money)
            .description(description)
            .exceptStatus(except)
            .categories(bookLineCategory)
            .memo(memo)
            .build();
    }
}
