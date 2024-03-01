package com.floney.floney.book.repository;

import com.floney.floney.analyze.dto.request.AnalyzeByCategoryRequest;
import com.floney.floney.analyze.dto.response.AnalyzeResponseByCategory;
import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.book.domain.category.entity.Subcategory;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookLine;
import com.floney.floney.book.domain.entity.BookUser;
import com.floney.floney.book.dto.process.BookLineExpense;
import com.floney.floney.book.dto.process.BookLineWithWriterView;
import com.floney.floney.book.dto.process.TotalExpense;
import com.floney.floney.book.dto.request.AllOutcomesRequest;
import com.floney.floney.common.domain.vo.DateDuration;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BookLineCustomRepository {

    List<BookLine> findAllByDurationOrderByDateDesc(String bookKey, DateDuration duration);

    List<BookLineWithWriterView> allLinesByDay(LocalDate date, String bookKey);

    void inactiveAllByRepeatBookLine(long repeatBookLineId);

    TotalExpense totalMoneyByDateAndCategoryType(String bookKey, LocalDate date, final CategoryType categoryType);

    List<BookLineExpense> findIncomeAndOutcomeByDurationPerDay(String bookKey, DateDuration dates);

    void inactiveAllBy(Book book);

    void inactiveAllByAfter(LocalDate localDate);

    List<BookLineWithWriterView> findAllOutcomes(AllOutcomesRequest request);

    double totalExpenseForBeforeMonth(AnalyzeByCategoryRequest request);

    List<AnalyzeResponseByCategory> analyzeByLineSubcategory(List<Subcategory> childCategories, DateDuration duration, String bookKey);

    double totalOutcomeMoneyForBudget(Book targetBook, DateDuration duration);

    Map<String, Double> totalExpensesForAsset(Book book, final LocalDate date);

    Optional<BookLine> findByIdWithCategories(Long id);

    Optional<BookLine> findByIdWithCategoriesAndWriter(Long id);

    List<BookLine> findAllByBookKeyOrderByDateDesc(String bookKey);

    void inactiveAllByBook(Book book);

    List<BookLine> findAllByBookUser(BookUser bookUser);

    double totalMoneyByDurationAndCategoryType(String bookKey, DateDuration duration, CategoryType categoryType);
}
