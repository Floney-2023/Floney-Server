package com.floney.floney.book.dto;

import com.floney.floney.book.entity.BookLine;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

import static com.floney.floney.book.service.CategoryEnum.ASSET;
import static com.floney.floney.book.service.CategoryEnum.FLOW;

@RequiredArgsConstructor
@Getter
public class BookLineResponse {

    private Long money;

    private String flow;

    private String asset;

    private String flowLine;

    private LocalDate lineDate;

    private String description;

    private Boolean except;

    private String repeat;

    private String nickname;

    @Builder
    private BookLineResponse(Long money, String flow, String asset, String flowLine, LocalDate lineDate, String description, Boolean except, String repeat, String nickname) {
        this.money = money;
        this.flow = flow;
        this.asset = asset;
        this.flowLine = flowLine;
        this.lineDate = lineDate;
        this.description = description;
        this.except = except;
        this.repeat = repeat;
        this.nickname = nickname;
    }

    public static BookLineResponse of(BookLine bookLine){
        return BookLineResponse.builder()
            .money(bookLine.getMoney())
            .flow(bookLine.getCategory().get(FLOW).getName())
            .asset(bookLine.getCategory().get(ASSET).getName())
            .flowLine(bookLine.getCategory().get(FLOW).getName())
            .except(bookLine.getExceptStatus())
            .description(bookLine.getDescription())
            .except(bookLine.getExceptStatus())
            .lineDate(bookLine.getLineDate())
            .build();


    }





}
