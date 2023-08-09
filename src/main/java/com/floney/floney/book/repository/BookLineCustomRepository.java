package com.floney.floney.book.repository;

import com.floney.floney.book.dto.request.AnalyzeByCategoryRequest;
import com.floney.floney.book.dto.response.AnalyzeByCategory;
import com.floney.floney.book.dto.process.BookLineExpense;
import com.floney.floney.book.dto.process.DayLine;
import com.floney.floney.book.dto.process.DayLineByDayView;
import com.floney.floney.book.dto.process.TotalExpense;
import com.floney.floney.book.dto.request.AllOutcomesRequest;
import com.floney.floney.book.dto.process.DatesDuration;
import com.floney.floney.book.entity.BookUser;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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

    List<AnalyzeByCategory> analyzeByCategory(AnalyzeByCategoryRequest request);

}
