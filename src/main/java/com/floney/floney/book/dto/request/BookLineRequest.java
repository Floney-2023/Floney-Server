package com.floney.floney.book.dto.request;

import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookLine;
import com.floney.floney.book.domain.entity.BookUser;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
@RequiredArgsConstructor
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


    @Builder
    private BookLineRequest(long lineId, String bookKey, double money, String flow, String asset, String line, LocalDate lineDate, String description, Boolean except) {
        this.lineId = lineId;
        this.bookKey = bookKey;
        this.money = money;
        this.flow = flow;
        this.asset = asset;
        this.line = line;
        this.lineDate = lineDate;
        this.description = description;
        this.except = except;
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
