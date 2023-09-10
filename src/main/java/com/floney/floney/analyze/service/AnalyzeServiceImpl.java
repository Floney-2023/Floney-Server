package com.floney.floney.analyze.service;

import com.floney.floney.analyze.dto.process.BookAnalyzer;
import com.floney.floney.analyze.dto.request.AnalyzeByCategoryRequest;
import com.floney.floney.analyze.dto.request.AnalyzeRequestByAsset;
import com.floney.floney.analyze.dto.request.AnalyzeRequestByBudget;
import com.floney.floney.analyze.dto.response.AnalyzeResponse;
import com.floney.floney.analyze.dto.response.AnalyzeResponseByAsset;
import com.floney.floney.analyze.dto.response.AnalyzeResponseByBudget;
import com.floney.floney.analyze.dto.response.AnalyzeResponseByCategory;
import com.floney.floney.book.entity.Asset;
import com.floney.floney.analyze.entity.BookAnalyze;
import com.floney.floney.book.entity.Budget;
import com.floney.floney.book.repository.AssetRepository;
import com.floney.floney.analyze.repository.BookAnalyzeRepository;
import com.floney.floney.book.repository.BudgetRepository;
import com.floney.floney.book.dto.process.DatesDuration;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.Category;
import com.floney.floney.book.repository.BookLineCustomRepository;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.category.CategoryRepository;
import com.floney.floney.book.util.DateFactory;
import com.floney.floney.common.constant.Status;
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
    private final BookLineCustomRepository bookLineRepository;
    private final BookAnalyzeRepository analyzeRepository;
    private final CategoryRepository categoryRepository;
    private final AssetRepository assetRepository;
    private final BudgetRepository budgetRepository;

    @Override
    @Transactional
    public AnalyzeResponse analyzeByCategory(AnalyzeByCategoryRequest request) {
        List<AnalyzeResponseByCategory> analyzeResultByCategory = bookLineRepository.analyzeByCategory(request);
        Book savedBook = findBook(request.getBookKey());

        BookAnalyze savedAnalyze = saveAnalyze(request, analyzeResultByCategory, savedBook);
        long difference = calculateDifference(request, savedAnalyze);

        return AnalyzeResponse.of(analyzeResultByCategory, savedAnalyze, difference);
    }

    @Override
    @Transactional(readOnly = true)
    public AnalyzeResponseByBudget analyzeByBudget(AnalyzeRequestByBudget request) {
        DatesDuration duration = DateFactory.getDateDuration(request.getDate());
        Book savedBook = findBook(request.getBookKey());

        // 자산 조회
        Budget budget = budgetRepository.findBudgetByBookAndDate(savedBook, LocalDate.parse(request.getDate()))
            .orElse(Budget.init());

        // 총 수입 조회
        Long totalIncome = bookLineRepository.totalIncomeMoneyForBudget(request, duration);
        return AnalyzeResponseByBudget.of(totalIncome, budget.getMoney());
    }

    @Override
    @Transactional(readOnly = true)
    public AnalyzeResponseByAsset analyzeByAsset(AnalyzeRequestByAsset request) {
        Book savedBook = findBook(request.getBookKey());

        // 예산 조회
        Asset asset = assetRepository.findAssetByBookAndDate(savedBook, LocalDate.parse(request.getDate()))
            .orElse(Asset.init());

        // 총 지출, 수입 조회
        Map<String, Long> totalExpense = bookLineRepository.totalExpensesForAsset(request);

        BookAnalyzer bookAnalyzer = new BookAnalyzer(totalExpense);
        return bookAnalyzer.analyzeAsset(asset.getMoney());
    }

    private Book findBook(String bookKey) {
        return bookRepository.findBookByBookKeyAndStatus(bookKey, Status.ACTIVE)
            .orElseThrow(() -> new NotFoundBookException(bookKey));
    }

    private long calculateDifference(AnalyzeByCategoryRequest request, BookAnalyze currentMonthAnalyze) {
        long beforeMonthTotal = bookLineRepository.totalExpenseForBeforeMonth(request);
        return currentMonthAnalyze.calculateDifferenceWith(beforeMonthTotal);
    }

    private BookAnalyze saveAnalyze(AnalyzeByCategoryRequest request, List<AnalyzeResponseByCategory> analyzeResult, Book book) {
        BookAnalyze analyze = BookAnalyze.builder()
            .analyzeDate(request.getLocalDate())
            .book(book)
            .category(findCategory(request.getRoot()))
            .analyzeResult(analyzeResult)
            .build();

        return analyzeRepository.save(analyze);
    }

    private Category findCategory(String root) {
        return categoryRepository.findFlowCategory(root)
            .orElseThrow(() -> new NotFoundCategoryException(root));
    }
}
