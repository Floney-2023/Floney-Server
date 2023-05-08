package com.floney.floney.book.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
public class CalendarLinesResponse {

    private static final int INCOME = 0;

    private static final int OUTCOME = 1;

    private List<BookLineExpense> expenses;

    private Long totalIncome;

    private Long totalOutcome;

    @Builder
    public CalendarLinesResponse(List<BookLineExpense> expenses, Long totalIncome, Long totalOutcome) {
        this.expenses = expenses;
        this.totalIncome = totalIncome;
        this.totalOutcome = totalOutcome;
    }

    public static CalendarLinesResponse of(List<BookLineExpense> expenses, List<CalendarTotalExpense> totalExpenses) {
        return CalendarLinesResponse.builder()
            .expenses(expenses)
            .totalIncome(totalExpenses.get(INCOME).getMoney())
            .totalOutcome(totalExpenses.get(OUTCOME).getMoney())
            .build();
    }
}
