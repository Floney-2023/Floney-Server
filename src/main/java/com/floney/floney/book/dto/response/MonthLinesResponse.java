package com.floney.floney.book.dto.response;

import com.floney.floney.book.dto.process.BookLineExpense;
import com.floney.floney.book.dto.process.MonthKey;
import com.floney.floney.book.util.DateFactory;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class MonthLinesResponse {

    private static final String INCOME = "수입";

    private static final String OUTCOME = "지출";

    private static final Long DEFAULT_MONEY = 0L;

    private final List<BookLineExpense> expenses;

    private final Long totalIncome;

    private final Long totalOutcome;

    @Builder
    public MonthLinesResponse(List<BookLineExpense> expenses, Long totalIncome, Long totalOutcome) {
        this.expenses = expenses;
        this.totalIncome = totalIncome;
        this.totalOutcome = totalOutcome;
    }

    public static MonthLinesResponse of(String monthDate, List<BookLineExpense> dayExpenses, Map<String, Long> totalExpenses) {
        return MonthLinesResponse.builder()
            .expenses(reflectDB(monthDate, dayExpenses))
            .totalIncome(totalExpenses.getOrDefault(INCOME, DEFAULT_MONEY))
            .totalOutcome(totalExpenses.getOrDefault(OUTCOME, DEFAULT_MONEY))
            .build();
    }

    public static List<BookLineExpense> reflectDB(String monthDate, List<BookLineExpense> dayExpenses) {
        Map<MonthKey, BookLineExpense> dates = DateFactory.initDates(monthDate);
        for (BookLineExpense dbExpense : dayExpenses) {
            dates.replace(MonthKey.toMonthKey(dbExpense), dbExpense);
        }
        return dates.values()
            .stream()
            .toList();
    }
}
