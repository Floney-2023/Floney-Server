package com.floney.floney.book.dto;

import com.floney.floney.book.entity.*;
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

    private String repeat;

    private String nickname;

    @Builder
    private CreateLineRequest(String bookKey, Long money, String flow, String asset, String line, LocalDate lineDate, String description, Boolean except, String repeat, String nickname) {
        this.bookKey = bookKey;
        this.money = money;
        this.flow = flow;
        this.asset = asset;
        this.line = line;
        this.lineDate = lineDate;
        this.description = description;
        this.except = except;
        this.repeat = repeat;
        this.nickname = nickname;
    }

    public BookLine to(BookUser bookUser) {
        return BookLine.builder()
            .book(bookUser.getBook())
            .lineDate(lineDate)
            .money(money)
            .exceptStatus(except)
            .writer(bookUser)
            .description(description)
            .build();
    }
}
