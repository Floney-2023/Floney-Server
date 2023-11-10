package com.floney.floney.book.dto.response;

import com.floney.floney.book.dto.process.BookLineExpense;
import com.floney.floney.book.dto.process.CarryOverInfo;
import com.floney.floney.book.dto.process.MonthKey;
import com.floney.floney.book.util.DateFactory;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

import static com.floney.floney.book.dto.process.MonthKey.toMonthKey;

@Getter
public class MonthLinesResponse {

    private static final String INCOME = "수입";

    private static final String OUTCOME = "지출";

    private static final float DEFAULT_MONEY = 0f;

    private final List<BookLineExpense> expenses;

    private final float totalIncome;

    private final float totalOutcome;

    private final CarryOverInfo carryOverInfo;

    @Builder
    public MonthLinesResponse(List<BookLineExpense> expenses, float totalIncome, float totalOutcome, CarryOverInfo carryOverInfo) {
        this.expenses = expenses;
        this.totalIncome = totalIncome;
        this.totalOutcome = totalOutcome;
        this.carryOverInfo = carryOverInfo;
    }

    public static MonthLinesResponse of(String monthDate, List<BookLineExpense> dayExpenses, Map<String, Float> totalExpenses, CarryOverInfo carryOverInfo) {
        return MonthLinesResponse.builder()
                .expenses(reflectDB(monthDate, dayExpenses))
                .totalIncome(totalExpenses.getOrDefault(INCOME, DEFAULT_MONEY))
                .totalOutcome(totalExpenses.getOrDefault(OUTCOME, DEFAULT_MONEY))
                .carryOverInfo(carryOverInfo)
                .build();
    }

    public static List<BookLineExpense> reflectDB(String monthDate, List<BookLineExpense> dayExpenses) {
        Map<MonthKey, BookLineExpense> initDatesFrame = DateFactory.initDates(monthDate);

        dayExpenses.forEach(dayExpense -> initDatesFrame.replace(toMonthKey(dayExpense), dayExpense));

        return initDatesFrame.values()
                .stream()
                .toList();
    }
}
