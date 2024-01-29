package com.floney.floney.book.domain.vo;

import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.book.dto.process.BookLineExpense;
import com.floney.floney.book.dto.process.CarryOverInfo;
import com.floney.floney.book.dto.process.MonthKey;
import com.floney.floney.common.domain.vo.DateDuration;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.floney.floney.book.dto.process.MonthKey.toMonthKey;
import static com.floney.floney.common.domain.vo.DateDuration.startAndEndOfMonth;

@Getter
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MonthLinesResponse {

    private static final String INCOME = "수입";
    private static final String OUTCOME = "지출";
    private static final double DEFAULT_MONEY = 0.0;
    private static final int ONE_DAY = 1;

    private final List<BookLineExpense> expenses;
    private final double totalIncome;
    private final double totalOutcome;
    private final CarryOverInfo carryOverInfo;

    public static MonthLinesResponse of(final String monthDate,
                                        final List<BookLineExpense> dayExpenses,
                                        final Map<String, Double> totalExpenses,
                                        final CarryOverInfo carryOverInfo) {
        return MonthLinesResponse.builder()
            .expenses(insertSavedDataToInitFrame(monthDate, dayExpenses))
            .totalIncome(totalExpenses.getOrDefault(INCOME, DEFAULT_MONEY))
            .totalOutcome(totalExpenses.getOrDefault(OUTCOME, DEFAULT_MONEY))
            .carryOverInfo(carryOverInfo)
            .build();
    }

    public static List<BookLineExpense> insertSavedDataToInitFrame(final String monthDate,
                                                                   final List<BookLineExpense> dayExpenses) {
        final Map<MonthKey, BookLineExpense> initDatesFrame = getInitBookLineExpenseByMonth(monthDate);

        for (final BookLineExpense bookLineExpense : dayExpenses) {
            initDatesFrame.replace(toMonthKey(bookLineExpense), bookLineExpense);
        }

        return initDatesFrame.values()
            .stream()
            .toList();
    }


    // 해당 월의 일별로 지출, 수입 초기화 객체를 만들어주는 메서드
    // return ex. { { "2024-01-09" : INCOME } : { date  : "2024-01-09" , money : 0.0 , assetType : INCOME } }
    // { { "2024-01-09" : OUTCOME } : { date  : "2024-01-09" , money : 0.0 , assetType : OUTCOME } }
    private static Map<MonthKey, BookLineExpense> getInitBookLineExpenseByMonth(final String targetDate) {
        final DateDuration dates = startAndEndOfMonth(targetDate);
        final Map<MonthKey, BookLineExpense> initDates = new LinkedHashMap<>();

        for (LocalDate currentDate = dates.getStartDate();
             !currentDate.isAfter(dates.getEndDate());
             currentDate = currentDate.plusDays(ONE_DAY)) {
            addExpense(initDates, currentDate, CategoryType.INCOME);
            addExpense(initDates, currentDate, CategoryType.OUTCOME);
        }
        return initDates;
    }

    private static void addExpense(final Map<MonthKey, BookLineExpense> initDates,
                                   final LocalDate currentDate,
                                   final CategoryType type) {
        initDates.put(
            MonthKey.of(currentDate, type),
            BookLineExpense.of(currentDate, type)
        );
    }
}
