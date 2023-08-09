package com.floney.floney.analyze.service;

import com.floney.floney.book.dto.response.AnalyzeResponse;
import com.floney.floney.book.dto.request.AnalyzeByCategoryRequest;
import com.floney.floney.book.dto.request.AnalyzeRequestByAsset;
import com.floney.floney.book.dto.request.AnalyzeRequestByBudget;
import com.floney.floney.book.dto.response.AnalyzeResponseByAsset;
import com.floney.floney.book.dto.response.AnalyzeResponseByBudget;
import com.floney.floney.book.entity.Book;

public interface AnalyzeService {
    AnalyzeResponse analyzeByCategory(AnalyzeByCategoryRequest request);

    AnalyzeResponseByBudget analyzeByBudget(AnalyzeRequestByBudget request);

    AnalyzeResponseByAsset analyzeByAsset(AnalyzeRequestByAsset request);

    Book findBook(String bookKey);
}
