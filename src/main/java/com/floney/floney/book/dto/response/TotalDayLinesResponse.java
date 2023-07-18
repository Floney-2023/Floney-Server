package com.floney.floney.book.dto.response;

import com.floney.floney.book.dto.process.TotalExpense;
import com.floney.floney.book.dto.process.DayLines;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class TotalDayLinesResponse {
    private final List<DayLines> dayLinesResponse;
    private final List<TotalExpense> totalExpense;
    private final boolean seeProfileImg;

    @Builder
    private TotalDayLinesResponse(List<DayLines> dayLinesResponse, List<TotalExpense> totalExpense, boolean seeProfileImg) {
        this.dayLinesResponse = dayLinesResponse;
        this.totalExpense = totalExpense;
        this.seeProfileImg = seeProfileImg;
    }

    public static TotalDayLinesResponse of(List<DayLines> dayLinesResponse, List<TotalExpense> totalExpense, boolean seeProfileImg) {
        return TotalDayLinesResponse.builder()
            .dayLinesResponse(dayLinesResponse)
            .totalExpense(totalExpense)
            .seeProfileImg(seeProfileImg)
            .build();
    }


}
