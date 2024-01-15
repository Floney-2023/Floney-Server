package com.floney.floney.book.domain.vo;

import com.floney.floney.book.dto.constant.AssetType;
import com.floney.floney.book.dto.process.BookLineExpense;
import com.floney.floney.book.dto.process.CarryOverInfo;
import com.floney.floney.book.dto.process.MonthKey;
import com.floney.floney.common.domain.vo.DateDuration;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.floney.floney.book.dto.process.MonthKey.toMonthKey;
import static com.floney.floney.common.domain.vo.DateDuration.getStartAndEndOfMonth;

@Getter
public class MonthLinesResponse {

    private static final String INCOME = "수입";

    private static final String OUTCOME = "지출";

    private static final double DEFAULT_MONEY = 0.0;

    private static final int ONE_DAY = 1;

    private final List<BookLineExpense> expenses;

    private final double totalIncome;

    private final double totalOutcome;

    private final CarryOverInfo carryOverInfo;

    @Builder
    public MonthLinesResponse(List<BookLineExpense> expenses, double totalIncome, double totalOutcome, CarryOverInfo carryOverInfo) {
        this.expenses = expenses;
        this.totalIncome = totalIncome;
        this.totalOutcome = totalOutcome;
        this.carryOverInfo = carryOverInfo;
    }

    public static MonthLinesResponse of(String monthDate, List<BookLineExpense> dayExpenses, Map<String, Double> totalExpenses, CarryOverInfo carryOverInfo) {
        return MonthLinesResponse.builder()
            .expenses(insertSavedDataToInitFrame(monthDate, dayExpenses))
            .totalIncome(totalExpenses.getOrDefault(INCOME, DEFAULT_MONEY))
            .totalOutcome(totalExpenses.getOrDefault(OUTCOME, DEFAULT_MONEY))
            .carryOverInfo(carryOverInfo)
            .build();
    }

    public static List<BookLineExpense> insertSavedDataToInitFrame(String monthDate, List<BookLineExpense> dayExpenses) {
        Map<MonthKey, BookLineExpense> initDatesFrame = getInitBookLineExpenseByMonth(monthDate);

        dayExpenses.forEach(dayExpense -> initDatesFrame.replace(toMonthKey(dayExpense), dayExpense));

        return initDatesFrame.values()
            .stream()
            .toList();
    }


    // 해당 월의 일별로 지출, 수입 초기화 객체를 만들어주는 메서드
    // return ex. { { "2024-01-09" : INCOME } : { date  : "2024-01-09" , money : 0.0 , assetType : INCOME } }
    // { { "2024-01-09" : OUTCOME } : { date  : "2024-01-09" , money : 0.0 , assetType : OUTCOME } }
    private static Map<MonthKey, BookLineExpense> getInitBookLineExpenseByMonth(String targetDate) {
        DateDuration dates = getStartAndEndOfMonth(targetDate);
        Map<MonthKey, BookLineExpense> initDates = new LinkedHashMap<>();
        LocalDate currentDate = dates.start();

        while (!currentDate.isAfter(dates.end())) {
            addExpense(initDates, currentDate, AssetType.INCOME);
            addExpense(initDates, currentDate, AssetType.OUTCOME);
            currentDate = currentDate.plusDays(ONE_DAY);
        }

        return initDates;
    }

    private static void addExpense(Map<MonthKey, BookLineExpense> initDates, LocalDate currentDate, AssetType type) {
        initDates.put(MonthKey.of(currentDate, type), BookLineExpense.initExpense(currentDate, type));
    }
}
