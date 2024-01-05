package com.floney.floney.book.dto.response;

import com.floney.floney.book.dto.process.CarryOverInfo;
import com.floney.floney.book.dto.process.DayLineResponse;
import com.floney.floney.book.dto.process.TotalExpense;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class TotalDayLinesResponse {
    private final List<DayLineResponse> dayLinesResponse;
    private final List<TotalExpense> totalExpense;
    private final boolean seeProfileImg;
    private final CarryOverInfo carryOverInfo;

    @Builder
    private TotalDayLinesResponse(List<DayLineResponse> dayLinesResponse, List<TotalExpense> totalExpense, boolean seeProfileImg, CarryOverInfo carryOverInfo) {
        this.dayLinesResponse = dayLinesResponse;
        this.totalExpense = totalExpense;
        this.seeProfileImg = seeProfileImg;
        this.carryOverInfo = carryOverInfo;
    }

    public static TotalDayLinesResponse of(List<DayLineResponse> dayLinesResponse, List<TotalExpense> totalExpense, boolean seeProfile, CarryOverInfo carryOverInfo) {
        return TotalDayLinesResponse.builder()
            .dayLinesResponse(dayLinesResponse)
            .totalExpense(totalExpense)
            .seeProfileImg(seeProfile)
            .carryOverInfo(carryOverInfo)
            .build();
    }


}
