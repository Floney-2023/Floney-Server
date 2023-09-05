package com.floney.floney.book.dto.response;

import com.floney.floney.book.entity.Book;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BookInfoResponse {
    private final String bookName;
    private final String bookImg;
    private final LocalDateTime startDay;
    private final long memberCount;

    @Builder
    private BookInfoResponse(String bookName, String bookImg, LocalDateTime startDay, long memberCount) {
        this.bookName = bookName;
        this.bookImg = bookImg;
        this.startDay = startDay;
        this.memberCount = memberCount;
    }

    public static BookInfoResponse of(Book book, long memberCount) {
        return BookInfoResponse.builder()
            .bookImg(book.getBookImg())
            .bookName(book.getName())
            .memberCount(memberCount)
            .startDay(book.getCreatedAt())
            .build();
    }
}
