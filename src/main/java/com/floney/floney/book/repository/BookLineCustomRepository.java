package com.floney.floney.book.repository;

import com.floney.floney.analyze.dto.request.AnalyzeByCategoryRequest;
import com.floney.floney.analyze.dto.request.AnalyzeRequestByAsset;
import com.floney.floney.analyze.dto.request.AnalyzeRequestByBudget;
import com.floney.floney.analyze.dto.response.AnalyzeResponseByCategory;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookLine;
import com.floney.floney.book.domain.entity.BookUser;
import com.floney.floney.book.domain.entity.Category;
import com.floney.floney.book.dto.process.BookLineExpense;
import com.floney.floney.book.dto.process.DayLine;
import com.floney.floney.book.dto.process.DayLineByDayView;
import com.floney.floney.book.dto.process.TotalExpense;
import com.floney.floney.book.dto.request.AllOutcomesRequest;
import com.floney.floney.common.domain.vo.DateDuration;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BookLineCustomRepository {

    Map<String, Double> totalExpenseByMonth(String bookKey, DateDuration dates);

    List<BookLine> findAllBookLineByDurationOrderByDateDesc(String bookKey, DateDuration duration);

    List<DayLineByDayView> allLinesByDay(LocalDate date, String bookKey);

    List<TotalExpense> totalExpenseByDay(LocalDate date, String bookKey);

    List<BookLineExpense> dayIncomeAndOutcome(String bookKey, DateDuration dates);

    void inactiveAllBy(String bookKey);

    List<DayLine> getAllLines(AllOutcomesRequest request);

    Double totalExpenseForBeforeMonth(AnalyzeByCategoryRequest request);

    List<AnalyzeResponseByCategory> analyzeByCategory(List<Category> childCategories, DateDuration duration, String bookKey);

    Double totalOutcomeMoneyForBudget(AnalyzeRequestByBudget request, DateDuration duration);

    Map<String, Double> totalExpensesForAsset(AnalyzeRequestByAsset request);

    Optional<BookLine> findByIdWithCategories(Long id);

    List<BookLine> findAllByBookOrderByDateDesc(String bookKey);

    void inactiveAllByBook(Book book);

    List<BookLine> findAllByBookUser(BookUser bookUser);
}
