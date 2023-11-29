package com.floney.floney.book.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class BudgetYearResponse {
    private LocalDate date;
    private double money;

    @QueryProjection
    public BudgetYearResponse(LocalDate date, double money) {
        this.date = date;
        this.money = money;
    }

}
