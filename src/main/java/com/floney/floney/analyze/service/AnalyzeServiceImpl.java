package com.floney.floney.analyze.service;

import com.floney.floney.analyze.dto.request.AnalyzeByCategoryRequest;
import com.floney.floney.analyze.dto.request.AnalyzeBySubcategoryRequest;
import com.floney.floney.analyze.dto.request.AnalyzeRequestByAsset;
import com.floney.floney.analyze.dto.request.AnalyzeRequestByBudget;
import com.floney.floney.analyze.dto.response.*;
import com.floney.floney.analyze.vo.Assets;
import com.floney.floney.book.domain.BookLines;
import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.book.domain.category.entity.Category;
import com.floney.floney.book.domain.category.entity.Subcategory;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookLine;
import com.floney.floney.book.domain.entity.Budget;
import com.floney.floney.book.repository.BookLineRepository;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.analyze.BudgetRepository;
import com.floney.floney.book.repository.category.CategoryRepository;
import com.floney.floney.common.domain.vo.DateDuration;
import com.floney.floney.common.exception.book.CannotAnalyzeException;
import com.floney.floney.common.exception.book.NotFoundBookException;
import com.floney.floney.common.exception.book.NotFoundCategoryException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static com.floney.floney.common.constant.Status.ACTIVE;

@Service
@Transactional
@RequiredArgsConstructor
public class AnalyzeServiceImpl implements AnalyzeService {

    private final BookRepository bookRepository;
    private final BookLineRepository bookLineRepository;
    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public AnalyzeResponse analyzeByCategory(final AnalyzeByCategoryRequest request) {
        // lineCategory 가 지출 또는 수입일 때만 분석
        final Category category = findCategoryByType(request.getRoot());
        validateCanAnalyze(category);

        final DateDuration duration = DateDuration.startAndEndOfMonth(request.getDate());
        final String bookKey = request.getBookKey();

        final List<Subcategory> subCategories = getSubCategoriesByParentAndBookKey(category, bookKey);

        // lineSubCategory 별로 가계부 내역 합계 조회
        final List<AnalyzeResponseByCategory> analyzeByCategory = bookLineRepository.analyzeByLineSubcategory(
            subCategories, duration, bookKey
        );
        final double totalMoney = calculateTotalMoney(analyzeByCategory);
        final double difference = calculateDifference(request, totalMoney);
        return AnalyzeResponse.of(analyzeByCategory, totalMoney, difference);
    }

    @Override
    @Transactional(readOnly = true)
    public AnalyzeResponseByBudget analyzeByBudget(final AnalyzeRequestByBudget request) {
        final DateDuration duration = DateDuration.startAndEndOfMonth(request.getDate());
        final Book book = findBook(request.getBookKey());

        // 자산 조회
        final Budget budget = budgetRepository.findBudgetByBookAndDateAndStatus(book, LocalDate.parse(request.getDate()), ACTIVE)
            .orElse(Budget.init());

        // 총 수입 조회
        final double totalOutcome = bookLineRepository.totalOutcomeMoneyForBudget(book, duration);
        return AnalyzeResponseByBudget.of(totalOutcome, budget.getMoney());
    }

    @Override
    @Transactional(readOnly = true)
    public AnalyzeResponseByAsset analyzeByAsset(final AnalyzeRequestByAsset request) {
        final Book book = findBook(request.getBookKey());
        final YearMonth currentMonth = request.getDate();

        final Assets assets = Assets.create(book.getAsset(), currentMonth);

        for (int month = 0; month < Assets.MONTHS; month++) {
            final YearMonth targetMonth = currentMonth.minusMonths(month);
            final double totalIncome = bookLineRepository.incomeMoneyForAssetUntil(book, targetMonth);
            final double totalOutcome = bookLineRepository.outcomeMoneyUntil(book, targetMonth);
            assets.update(targetMonth, totalIncome - totalOutcome);
        }

        final double incomeOfThisMonth = bookLineRepository.incomeMoneyForAssetByMonth(book, currentMonth);
        final double outcomeOfThisMonth = bookLineRepository.outcomeMoneyByMonth(book, currentMonth);

        return AnalyzeResponseByAsset.of(incomeOfThisMonth - outcomeOfThisMonth, book.getAsset(), assets);
    }

    @Override
    @Transactional(readOnly = true)
    public AnalyzeResponseBySubcategory analyzeByLineSubcategory(final AnalyzeBySubcategoryRequest request) {
        final DateDuration duration = DateDuration.startAndEndOfMonth(request.getYearMonth());
        final List<BookLine> bookLines = bookLineRepository.findAllByDurationAndLineSubcategoryAndWriters(
            request.getBookKey(), duration, request.getCategory(), request.getSubcategory(), request.getEmails()
        );
        final List<BookLine> sortedBookLines = BookLines.from(bookLines).sort(request.getSortingType());
        final List<BookLineResponse> bookLineResponses = sortedBookLines.stream().map(BookLineResponse::from).toList();

        // Get categoryKey from first bookLine if available, otherwise use request value
        final String subcategoryKey = bookLines.isEmpty()
            ? request.getSubcategory()
            : getCategoryKey(bookLines.get(0).getCategories().getLineSubcategory());

        return AnalyzeResponseBySubcategory.of(subcategoryKey, bookLineResponses);
    }

    /**
     * Returns categoryKey if available (for default categories),
     * otherwise returns name (for user-defined categories)
     */
    private String getCategoryKey(final com.floney.floney.book.domain.category.entity.Subcategory subcategory) {
        String categoryKey = subcategory.getCategoryKey();
        return categoryKey != null ? categoryKey : subcategory.getName();
    }

    private void validateCanAnalyze(final Category category) {
        if (!category.isIncomeOrOutcome()) {
            throw new CannotAnalyzeException(category.getName());
        }
    }

    private Category findCategoryByType(final CategoryType categoryType) {
        return categoryRepository.findByType(categoryType)
            .orElseThrow(() -> new NotFoundCategoryException(categoryType.getMeaning()));
    }

    private Book findBook(String bookKey) {
        return bookRepository.findBookByBookKeyAndStatus(bookKey, ACTIVE)
            .orElseThrow(() -> new NotFoundBookException(bookKey));
    }

    private double calculateDifference(AnalyzeByCategoryRequest request, double totalMoney) {
        double beforeMonthTotal = bookLineRepository.totalExpenseForBeforeMonth(request);
        return totalMoney - beforeMonthTotal;
    }

    private List<Subcategory> getSubCategoriesByParentAndBookKey(final Category parent,
                                                                 final String bookKey) {
        return categoryRepository.findSubcategories(parent, bookKey);
    }

    private double calculateTotalMoney(List<AnalyzeResponseByCategory> result) {
        return result.stream()
            .mapToDouble(AnalyzeResponseByCategory::getMoney)
            .sum();
    }
}
