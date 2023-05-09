package com.floney.floney.book.dto;

import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.BookLine;
import com.floney.floney.book.entity.BookUser;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class CreateLineRequest {

    private String bookKey;

    private Long money;

    private String flow;

    private String asset;

    private String line;

    private LocalDate lineDate;

    private String description;

    private Boolean except;

    private String nickname;

    @Builder
    private CreateLineRequest(String bookKey, Long money, String flow, String asset, String line, LocalDate lineDate, String description, Boolean except, String nickname) {
        this.bookKey = bookKey;
        this.money = money;
        this.flow = flow;
        this.asset = asset;
        this.line = line;
        this.lineDate = lineDate;
        this.description = description;
        this.except = except;
        this.nickname = nickname;
    }

    public BookLine to(BookUser bookUser, Book book) {
        return BookLine.builder()
            .book(book)
            .lineDate(lineDate)
            .money(money)
            .exceptStatus(except)
            .writer(bookUser)
            .description(description)
            .build();
    }

}
