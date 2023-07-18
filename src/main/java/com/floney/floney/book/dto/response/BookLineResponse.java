package com.floney.floney.book.dto.response;

import com.floney.floney.book.entity.BookLine;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

import static com.floney.floney.book.dto.constant.CategoryEnum.*;

@Getter
public class BookLineResponse {

    private final Long money;

    private final String flow;

    private final String asset;

    private final String line;

    private final LocalDate lineDate;

    private final String description;

    private final Boolean except;

    private final String nickname;

    @Builder
    private BookLineResponse(Long money, String flow, String asset, String line, LocalDate lineDate, String description, Boolean except, String nickname) {
        this.money = money;
        this.flow = flow;
        this.asset = asset;
        this.line = line;
        this.lineDate = lineDate;
        this.description = description;
        this.except = except;
        this.nickname = nickname;
    }

    public static BookLineResponse of(BookLine bookLine) {
        return BookLineResponse.builder()
            .money(bookLine.getMoney())
            .flow(bookLine.getTargetCategory(FLOW))
            .asset(bookLine.getTargetCategory(ASSET))
            .line(bookLine.getTargetCategory(FLOW_LINE))
            .lineDate(bookLine.getLineDate())
            .description(bookLine.getDescription())
            .except(bookLine.getExceptStatus())
            .nickname(bookLine.getWriter().getNickName())
            .build();
    }


}
