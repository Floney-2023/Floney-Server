package com.floney.floney.book.repository;

import com.floney.floney.book.dto.request.*;
import com.floney.floney.book.dto.response.AnalyzeResponseByCategory;
import com.floney.floney.book.dto.process.BookLineExpense;
import com.floney.floney.book.dto.process.DayLine;
import com.floney.floney.book.dto.process.DayLineByDayView;
import com.floney.floney.book.dto.process.TotalExpense;
import com.floney.floney.book.dto.response.AnalyzeResponseByBudget;
import com.floney.floney.book.entity.BookUser;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface BookLineCustomRepository {

    Map<String, Long> totalExpenseByMonth(String bookKey, DatesDuration dates);

    List<DayLineByDayView> allLinesByDay(LocalDate date, String bookKey);

    List<TotalExpense> totalExpenseByDay(LocalDate date, String bookKey);

    List<BookLineExpense> dayIncomeAndOutcome(String bookKey, DatesDuration dates);

    void deleteAllLines(String bookKey);

    List<DayLine> allOutcomes(AllOutcomesRequest request);

    void deleteAllLinesByUser(BookUser bookUser, String bookKey);

    Long totalExpenseForBeforeMonth(AnalyzeByCategoryRequest request);

    List<AnalyzeResponseByCategory> analyzeByCategory(AnalyzeByCategoryRequest request);

    AnalyzeResponseByBudget totalIncomeForBudget(AnalyzeRequestByBudget request, DatesDuration duration);

    Map<String,Long> totalExpensesForAsset(AnalyzeRequestByAsset request, DatesDuration duration);
}
