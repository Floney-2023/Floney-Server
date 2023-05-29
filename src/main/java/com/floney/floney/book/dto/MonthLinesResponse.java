package com.floney.floney.book.dto;

import com.floney.floney.book.util.DateFactory;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class MonthLinesResponse {

    private static final int INCOME = 0;

    private static final int OUTCOME = 1;

    private final List<BookLineExpense> expenses;

    private final Long totalIncome;

    private final Long totalOutcome;

    @Builder
    public MonthLinesResponse(List<BookLineExpense> expenses, Long totalIncome, Long totalOutcome) {
        this.expenses = expenses;
        this.totalIncome = totalIncome;
        this.totalOutcome = totalOutcome;
    }

    public static MonthLinesResponse of(String monthDate, List<BookLineExpense> expenses, List<TotalExpense> totalExpenses) {
        return MonthLinesResponse.builder()
            .expenses(reflectDB(monthDate, expenses))
            .totalIncome(totalExpenses.get(INCOME).getMoney())
            .totalOutcome(totalExpenses.get(OUTCOME).getMoney())
            .build();
    }

    public static List<BookLineExpense> reflectDB(String monthDate, List<BookLineExpense> expenses) {
        Map<MonthKey, BookLineExpense> dates = DateFactory.initDates(monthDate);
        for (BookLineExpense dbExpense : expenses) {
            dates.replace(MonthKey.toMonthKey(dbExpense), dbExpense);
        }
        return dates.values()
            .stream()
            .toList();
    }
}
