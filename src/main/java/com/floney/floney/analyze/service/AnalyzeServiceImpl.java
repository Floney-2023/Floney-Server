package com.floney.floney.analyze.service;

import com.floney.floney.analyze.dto.process.BookAnalyzer;
import com.floney.floney.analyze.dto.request.AnalyzeByCategoryRequest;
import com.floney.floney.analyze.dto.request.AnalyzeRequestByAsset;
import com.floney.floney.analyze.dto.request.AnalyzeRequestByBudget;
import com.floney.floney.analyze.dto.response.AnalyzeResponse;
import com.floney.floney.analyze.dto.response.AnalyzeResponseByAsset;
import com.floney.floney.analyze.dto.response.AnalyzeResponseByBudget;
import com.floney.floney.analyze.dto.response.AnalyzeResponseByCategory;
import com.floney.floney.book.dto.process.DatesDuration;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.Budget;
import com.floney.floney.book.repository.BookLineCustomRepository;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.BudgetRepository;
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

    @Override
    @Transactional
    public AnalyzeResponse analyzeByCategory(AnalyzeByCategoryRequest request) {
        List<AnalyzeResponseByCategory> analyzeResultByCategory = bookLineRepository.analyzeByCategory(request);

        long totalMoney = calculateTotalMoney(analyzeResultByCategory);
        long difference = calculateDifference(request, totalMoney);
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
        Long totalIncome = bookLineRepository.totalIncomeMoneyForBudget(request, duration);
        return AnalyzeResponseByBudget.of(totalIncome, budget.getMoney());
    }

    @Override
    @Transactional(readOnly = true)
    public AnalyzeResponseByAsset analyzeByAsset(AnalyzeRequestByAsset request) {
        Book savedBook = findBook(request.getBookKey());

        // 총 지출, 수입 조회
        Map<String, Long> totalExpense = bookLineRepository.totalExpensesForAsset(request);

        BookAnalyzer bookAnalyzer = new BookAnalyzer(totalExpense);
        return bookAnalyzer.analyzeAsset(savedBook.getAsset());
    }

    private Book findBook(String bookKey) {
        return bookRepository.findBookExclusivelyByBookKeyAndStatus(bookKey, Status.ACTIVE)
            .orElseThrow(() -> new NotFoundBookException(bookKey));
    }

    private long calculateDifference(AnalyzeByCategoryRequest request, Long totalMoney) {
        long beforeMonthTotal = bookLineRepository.totalExpenseForBeforeMonth(request);
        return totalMoney - beforeMonthTotal;
    }

    private Long calculateTotalMoney(List<AnalyzeResponseByCategory> result) {
        return result.stream()
            .mapToLong(AnalyzeResponseByCategory::getMoney)
            .sum();
    }
}
