package com.floney.floney.analyze.service;

import com.floney.floney.analyze.dto.process.BookAnalyzer;
import com.floney.floney.analyze.dto.request.AnalyzeByCategoryRequest;
import com.floney.floney.analyze.dto.request.AnalyzeRequestByAsset;
import com.floney.floney.analyze.dto.request.AnalyzeRequestByBudget;
import com.floney.floney.analyze.dto.response.AnalyzeResponse;
import com.floney.floney.analyze.dto.response.AnalyzeResponseByAsset;
import com.floney.floney.analyze.dto.response.AnalyzeResponseByBudget;
import com.floney.floney.analyze.dto.response.AnalyzeResponseByCategory;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.Budget;
import com.floney.floney.book.domain.entity.Category;
import com.floney.floney.book.domain.entity.category.BookCategory;
import com.floney.floney.book.dto.process.AssetInfo;
import com.floney.floney.book.repository.BookLineRepository;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.analyze.BudgetRepository;
import com.floney.floney.book.repository.category.CategoryRepository;
import com.floney.floney.common.constant.Status;
import com.floney.floney.common.domain.vo.DateDuration;
import com.floney.floney.common.exception.book.NotFoundBookException;
import com.floney.floney.common.exception.book.NotFoundCategoryException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyzeServiceImpl implements AnalyzeService {

    private final BookRepository bookRepository;
    private final BookLineRepository bookLineRepository;
    private final BudgetRepository budgetRepository;
    private final AssetServiceImpl assetFactory;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public AnalyzeResponse analyzeByCategory(AnalyzeByCategoryRequest request) {

        // 분석 종류 - 지출 or 수입
        Category rootCategory = categoryRepository.findParentCategory(request.getRoot())
                .orElseThrow(() -> new NotFoundCategoryException(request.getRoot()));
        DateDuration duration = DateDuration.getStartAndEndOfMonth(request.getDate());
        String bookKey = request.getBookKey();

        // 부모가 지출 or 수입인 자식 카테고리 조회
        List<Category> childCategoriesByRoot = getAllChildCategoryByRoot(rootCategory, bookKey);

        // 카테고리 별, 가계부 내역 합계 조회
        List<AnalyzeResponseByCategory> analyzeResultByCategory = bookLineRepository.analyzeByCategory(childCategoriesByRoot, duration, bookKey);

        double totalMoney = calculateTotalMoney(analyzeResultByCategory);
        double difference = calculateDifference(request, totalMoney);
        return AnalyzeResponse.of(analyzeResultByCategory, totalMoney, difference);
    }

    @Override
    @Transactional(readOnly = true)
    public AnalyzeResponseByBudget analyzeByBudget(AnalyzeRequestByBudget request) {
        DateDuration duration = DateDuration.getStartAndEndOfMonth(request.getDate());
        Book savedBook = findBook(request.getBookKey());

        // 자산 조회
        Budget budget = budgetRepository.findBudgetByBookAndDate(savedBook, LocalDate.parse(request.getDate()))
                .orElse(Budget.init());

        // 총 수입 조회
        double totalOutcome = bookLineRepository.totalOutcomeMoneyForBudget(request, duration);
        return AnalyzeResponseByBudget.of(totalOutcome, budget.getMoney());
    }

    @Override
    @Transactional(readOnly = true)
    public AnalyzeResponseByAsset analyzeByAsset(AnalyzeRequestByAsset request) {
        Book savedBook = findBook(request.getBookKey());

        // 총 지출, 수입 조회
        Map<String, Double> totalExpense = bookLineRepository.totalExpensesForAsset(request);

        BookAnalyzer bookAnalyzer = new BookAnalyzer(totalExpense);
        Map<LocalDate, AssetInfo> assetInfo = assetFactory.getAssetInfo(savedBook, request.getDate());
        return bookAnalyzer.analyzeAsset(request.getDate(), savedBook.getAsset(), assetInfo);
    }

    private Book findBook(String bookKey) {
        return bookRepository.findBookByBookKeyAndStatus(bookKey, Status.ACTIVE)
                .orElseThrow(() -> new NotFoundBookException(bookKey));
    }

    private double calculateDifference(AnalyzeByCategoryRequest request, double totalMoney) {
        double beforeMonthTotal = bookLineRepository.totalExpenseForBeforeMonth(request);
        return totalMoney - beforeMonthTotal;
    }

    private List<Category> getAllChildCategoryByRoot(Category rootCategory, String bookKey) {
        List<Category> childCategoriesByRoot = categoryRepository.findAllDefaultChildCategoryByRoot(rootCategory);
        List<BookCategory> customCategories = categoryRepository.findAllCustomChildCategoryByRootAndRoot(rootCategory, bookKey);
        childCategoriesByRoot.addAll(customCategories);
        return childCategoriesByRoot;
    }

    private double calculateTotalMoney(List<AnalyzeResponseByCategory> result) {
        return result.stream()
                .mapToDouble(AnalyzeResponseByCategory::getMoney)
                .sum();
    }
}
