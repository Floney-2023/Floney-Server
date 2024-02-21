package com.floney.floney.book.dto.response;

import com.floney.floney.book.dto.process.BookLineWithWriterView;
import com.floney.floney.book.dto.process.CarryOverInfo;
import com.floney.floney.book.dto.process.TotalExpense;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TotalDayLinesResponse {

    private final List<BookLineWithWriterView> dayLinesResponse;
    private final List<TotalExpense> totalExpense;
    private final boolean seeProfileImg;
    private final CarryOverInfo carryOverInfo;

    public static TotalDayLinesResponse of(final List<BookLineWithWriterView> bookLinesWithWriter,
                                           final List<TotalExpense> totalExpense,
                                           final boolean seeProfile,
                                           final CarryOverInfo carryOverInfo) {
        return TotalDayLinesResponse.builder()
            .dayLinesResponse(bookLinesWithWriter)
            .totalExpense(totalExpense)
            .seeProfileImg(seeProfile)
            .carryOverInfo(carryOverInfo)
            .build();
    }
}
