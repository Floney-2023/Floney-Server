package com.floney.floney.book.dto.response;

import com.floney.floney.book.domain.entity.Book;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BookInfoResponse {
    private final String bookName;
    private final String bookImg;
    private final LocalDateTime startDay;
    private final long memberCount;
    private final String bookKey;

    @Builder
    private BookInfoResponse(String bookName, String bookImg, LocalDateTime startDay, long memberCount,String bookKey) {
        this.bookName = bookName;
        this.bookImg = bookImg;
        this.startDay = startDay;
        this.memberCount = memberCount;
        this.bookKey = bookKey;
    }

    public static BookInfoResponse of(Book book, long memberCount) {
        return BookInfoResponse.builder()
                .bookImg(book.getBookImg())
                .bookName(book.getName())
                .memberCount(memberCount)
                .startDay(book.getCreatedAt())
                .bookKey(book.getBookKey())
                .build();
    }
}
