package com.floney.floney.book.repository;

import com.floney.floney.analyze.dto.request.AnalyzeByCategoryRequest;
import com.floney.floney.analyze.dto.request.AnalyzeRequestByAsset;
import com.floney.floney.analyze.dto.request.AnalyzeRequestByBudget;
import com.floney.floney.analyze.dto.response.AnalyzeResponseByCategory;
import com.floney.floney.book.dto.process.*;
import com.floney.floney.book.dto.request.AllOutcomesRequest;
import com.floney.floney.book.entity.BookLine;
import com.floney.floney.book.entity.BookLineCategory;
import com.floney.floney.book.entity.BookUser;
import com.floney.floney.settlement.domain.entity.Settlement;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BookLineCustomRepository {

    Map<String, Long> totalExpenseByMonth(String bookKey, DatesDuration dates);

    Map<String, Long> totalExpenseByAll(String bookKey);

    List<DayLineByDayView> allLinesByDay(LocalDate date, String bookKey);

    List<TotalExpense> totalExpenseByDay(LocalDate date, String bookKey);

    List<BookLineExpense> dayIncomeAndOutcome(String bookKey, DatesDuration dates);

    void deleteAllLines(String bookKey);

    List<DayLine> allOutcomes(AllOutcomesRequest request);

    void deleteAllLinesByUser(BookUser bookUser, String bookKey);

    Long totalExpenseForBeforeMonth(AnalyzeByCategoryRequest request);

    List<AnalyzeResponseByCategory> analyzeByCategory(AnalyzeByCategoryRequest request);

    Long totalIncomeMoneyForBudget(AnalyzeRequestByBudget request, DatesDuration duration);

    Map<String, Long> totalExpensesForAsset(AnalyzeRequestByAsset request);

    Optional<BookLine> findByIdWithCategories(Long id);

    List<BookLine> findLineHaveToDelete();

    List<BookLineCategory> findCategoryHaveToDelete();

    List<Settlement> findSettlementHaveToDelete();
}
