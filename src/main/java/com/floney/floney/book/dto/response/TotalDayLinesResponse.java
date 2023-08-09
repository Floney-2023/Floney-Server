package com.floney.floney.book.dto.response;

import com.floney.floney.book.dto.process.TotalExpense;
import com.floney.floney.book.dto.process.DayLines;
import com.floney.floney.book.entity.Book;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class TotalDayLinesResponse {
    private final List<DayLines> dayLinesResponse;
    private final List<TotalExpense> totalExpense;
    private final boolean seeProfileImg;
    private final long carryOverMoney;

    @Builder
    private TotalDayLinesResponse(List<DayLines> dayLinesResponse, List<TotalExpense> totalExpense, boolean seeProfileImg, long carryOverMoney) {
        this.dayLinesResponse = dayLinesResponse;
        this.totalExpense = totalExpense;
        this.seeProfileImg = seeProfileImg;
        this.carryOverMoney = carryOverMoney;
    }

    public static TotalDayLinesResponse of(List<DayLines> dayLinesResponse, List<TotalExpense> totalExpense, Book book) {
        return TotalDayLinesResponse.builder()
            .dayLinesResponse(dayLinesResponse)
            .totalExpense(totalExpense)
            .seeProfileImg(book.getSeeProfile())
            .build();
    }

    public static TotalDayLinesResponse firstDayOf(List<DayLines> dayLinesResponse, List<TotalExpense> totalExpense, Book book) {
        return TotalDayLinesResponse.builder()
            .dayLinesResponse(dayLinesResponse)
            .totalExpense(totalExpense)
            .seeProfileImg(book.getSeeProfile())
            .carryOverMoney(book.getCarryOverMoney())
            .build();
    }


}
