package com.floney.floney.analyze.service;

import com.floney.floney.analyze.dto.request.AnalyzeByCategoryRequest;
import com.floney.floney.analyze.dto.request.AnalyzeRequestByAsset;
import com.floney.floney.analyze.dto.request.AnalyzeRequestByBudget;
import com.floney.floney.analyze.dto.response.AnalyzeResponse;
import com.floney.floney.analyze.dto.response.AnalyzeResponseByAsset;
import com.floney.floney.analyze.dto.response.AnalyzeResponseByBudget;

public interface AnalyzeService {
    AnalyzeResponse analyzeByCategory(AnalyzeByCategoryRequest request);

    AnalyzeResponseByBudget analyzeByBudget(AnalyzeRequestByBudget request);

    AnalyzeResponseByAsset analyzeByAsset(AnalyzeRequestByAsset request);

}
