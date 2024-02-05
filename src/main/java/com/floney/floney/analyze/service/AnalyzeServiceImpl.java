package com.floney.floney.analyze.service;

import com.floney.floney.analyze.dto.process.BookAnalyzer;
import com.floney.floney.analyze.dto.request.AnalyzeByCategoryRequest;
import com.floney.floney.analyze.dto.request.AnalyzeRequestByAsset;
import com.floney.floney.analyze.dto.request.AnalyzeRequestByBudget;
import com.floney.floney.analyze.dto.response.AnalyzeResponse;
import com.floney.floney.analyze.dto.response.AnalyzeResponseByAsset;
import com.floney.floney.analyze.dto.response.AnalyzeResponseByBudget;
import com.floney.floney.analyze.dto.response.AnalyzeResponseByCategory;
import com.floney.floney.book.domain.category.entity.Category;
import com.floney.floney.book.domain.category.entity.Subcategory;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.Budget;
import com.floney.floney.book.dto.process.AssetInfo;
import com.floney.floney.book.repository.BookLineRepository;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.analyze.BudgetRepository;
import com.floney.floney.book.repository.category.CategoryRepository;
import com.floney.floney.common.constant.Status;
import com.floney.floney.common.domain.vo.DateDuration;
import com.floney.floney.common.exception.book.CannotAnalyzeException;
import com.floney.floney.common.exception.book.NotFoundBookException;
import com.floney.floney.common.exception.book.NotFoundCategoryException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class AnalyzeServiceImpl implements AnalyzeService {

    private final BookRepository bookRepository;
    private final BookLineRepository bookLineRepository;
    private final BudgetRepository budgetRepository;
    private final AssetServiceImpl assetFactory;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public AnalyzeResponse analyzeByCategory(final AnalyzeByCategoryRequest request) {
        // lineCategory 가 지출 또는 수입일 때만 분석
        final Category category = findCategory(request.getRoot());
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
        final Budget budget = budgetRepository.findBudgetByBookAndDate(book, LocalDate.parse(request.getDate()))
            .orElse(Budget.init());

        // 총 수입 조회
        final double totalOutcome = bookLineRepository.totalOutcomeMoneyForBudget(book, duration);
        return AnalyzeResponseByBudget.of(totalOutcome, budget.getMoney());
    }

    @Override
    @Transactional(readOnly = true)
    public AnalyzeResponseByAsset analyzeByAsset(final AnalyzeRequestByAsset request) {
        final Book savedBook = findBook(request.getBookKey());

        // 총 지출, 수입 조회
        final Map<String, Double> totalExpense = bookLineRepository.totalExpensesForAsset(request);

        final BookAnalyzer bookAnalyzer = new BookAnalyzer(totalExpense);
        final Map<LocalDate, AssetInfo> assetInfo = assetFactory.getAssetInfo(savedBook, request.getDate());
        return bookAnalyzer.analyzeAsset(request.getDate(), savedBook.getAsset(), assetInfo);
    }

    private void validateCanAnalyze(final Category category) {
        if (!category.isIncomeOrOutcome()) {
            throw new CannotAnalyzeException(category.getName());
        }
    }

    private Category findCategory(final String name) {
        return categoryRepository.findParentCategory(name)
            .orElseThrow(() -> new NotFoundCategoryException(name));
    }

    private Book findBook(String bookKey) {
        return bookRepository.findBookByBookKeyAndStatus(bookKey, Status.ACTIVE)
            .orElseThrow(() -> new NotFoundBookException(bookKey));
    }

    private double calculateDifference(AnalyzeByCategoryRequest request, double totalMoney) {
        double beforeMonthTotal = bookLineRepository.totalExpenseForBeforeMonth(request);
        return totalMoney - beforeMonthTotal;
    }

    private List<Subcategory> getSubCategoriesByParentAndBookKey(final Category parent,
                                                                 final String bookKey) {
        return categoryRepository.findAllLineSubCategoryByLineCategory(parent, bookKey);
    }

    private double calculateTotalMoney(List<AnalyzeResponseByCategory> result) {
        return result.stream()
            .mapToDouble(AnalyzeResponseByCategory::getMoney)
            .sum();
    }
}
