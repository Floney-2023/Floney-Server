package com.floney.floney.book.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class TotalDayLinesResponse {
    private final List<DayLines> dayLinesResponse;
    private final List<TotalExpense> totalExpense;

    private TotalDayLinesResponse(List<DayLines> dayLinesResponse, List<TotalExpense> totalExpense) {
        this.dayLinesResponse = dayLinesResponse;
        this.totalExpense = totalExpense;
    }

    public static TotalDayLinesResponse of(List<DayLines> dayLinesResponse, List<TotalExpense> totalExpense){
        return new TotalDayLinesResponse(dayLinesResponse,totalExpense);
    }

}
