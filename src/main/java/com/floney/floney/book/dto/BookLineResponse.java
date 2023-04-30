package com.floney.floney.book.dto;

import com.floney.floney.book.entity.BookLine;
import com.floney.floney.book.service.CategoryEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

import static com.floney.floney.book.service.CategoryEnum.*;

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

    private String nickname;

    @Builder
    private BookLineResponse(Long money, String flow, String asset, String flowLine, LocalDate lineDate, String description, Boolean except,String nickname) {
        this.money = money;
        this.flow = flow;
        this.asset = asset;
        this.flowLine = flowLine;
        this.lineDate = lineDate;
        this.description = description;
        this.except = except;
        this.nickname = nickname;
    }

    public static BookLineResponse of(BookLine bookLine){
        return BookLineResponse.builder()
            .money(bookLine.getMoney())
            .flow(bookLine.getTargetCategory(FLOW))
            .asset(bookLine.getTargetCategory(ASSET))
            .flowLine(bookLine.getTargetCategory(FLOW_LINE))
            .lineDate(bookLine.getLineDate())
            .description(bookLine.getDescription())
            .except(bookLine.getExceptStatus())
            .nickname(bookLine.getWriter().getNickName())
            .build();
    }


}
