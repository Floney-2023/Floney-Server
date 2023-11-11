package com.floney.floney.analyze.service;

import com.floney.floney.analyze.dto.process.BookAnalyzer;
import com.floney.floney.analyze.dto.request.AnalyzeByCategoryRequest;
import com.floney.floney.analyze.dto.request.AnalyzeRequestByAsset;
import com.floney.floney.analyze.dto.request.AnalyzeRequestByBudget;
import com.floney.floney.analyze.dto.response.AnalyzeResponse;
import com.floney.floney.analyze.dto.response.AnalyzeResponseByAsset;
import com.floney.floney.analyze.dto.response.AnalyzeResponseByBudget;
import com.floney.floney.analyze.dto.response.AnalyzeResponseByCategory;
import com.floney.floney.book.dto.process.AssetInfo;
import com.floney.floney.book.dto.process.DatesDuration;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.Budget;
import com.floney.floney.book.repository.BookLineCustomRepository;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.analyze.BudgetRepository;
import com.floney.floney.book.util.DateFactory;
import com.floney.floney.common.constant.Status;
import com.floney.floney.common.exception.book.NotFoundBookException;
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
    private final BudgetRepository budgetRepository;
    private final AssetServiceImpl assetFactory;

    @Override
    @Transactional
    public AnalyzeResponse analyzeByCategory(AnalyzeByCategoryRequest request) {
        List<AnalyzeResponseByCategory> analyzeResultByCategory = bookLineRepository.analyzeByCategory(request);

        float totalMoney = (float) calculateTotalMoney(analyzeResultByCategory);
        float difference = calculateDifference(request, totalMoney);
        return AnalyzeResponse.of(analyzeResultByCategory, totalMoney, difference);
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
        float totalOutcome = bookLineRepository.totalOutcomeMoneyForBudget(request, duration);
        return AnalyzeResponseByBudget.of(totalOutcome, budget.getMoney());
    }

    @Override
    @Transactional(readOnly = true)
    public AnalyzeResponseByAsset analyzeByAsset(AnalyzeRequestByAsset request) {
        Book savedBook = findBook(request.getBookKey());

        // 총 지출, 수입 조회
        Map<String, Float> totalExpense = bookLineRepository.totalExpensesForAsset(request);

        BookAnalyzer bookAnalyzer = new BookAnalyzer(totalExpense);
        Map<LocalDate, AssetInfo> assetInfo = assetFactory.getAssetInfo(savedBook, request.getDate());
        return bookAnalyzer.analyzeAsset(savedBook.getAsset(), assetInfo);
    }

    private Book findBook(String bookKey) {
        return bookRepository.findBookByBookKeyAndStatus(bookKey, Status.ACTIVE)
            .orElseThrow(() -> new NotFoundBookException(bookKey));
    }

    private float calculateDifference(AnalyzeByCategoryRequest request, float totalMoney) {
        float beforeMonthTotal = bookLineRepository.totalExpenseForBeforeMonth(request);
        return totalMoney - beforeMonthTotal;
    }

    private double calculateTotalMoney(List<AnalyzeResponseByCategory> result) {
        return result.stream()
            .mapToDouble(AnalyzeResponseByCategory::getMoney)
            .sum();
    }
}
