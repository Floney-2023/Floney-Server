package com.floney.floney.analyze.controller;

import com.floney.floney.analyze.service.AnalyzeService;
import com.floney.floney.analyze.dto.request.AnalyzeByCategoryRequest;
import com.floney.floney.analyze.dto.request.AnalyzeRequestByAsset;
import com.floney.floney.analyze.dto.request.AnalyzeRequestByBudget;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/analyze")
public class AnalyzeController {
    private final AnalyzeService analyzeService;

    /**
     * 
     * @body AnalyzeByCategoryRequest 카테고리 별 분석 요청
     * @return AnalyzeResponse 해당 카테고리 분석 결과
     */
    @GetMapping("/category")
    public ResponseEntity<?> analyzeByCategory(@RequestBody AnalyzeByCategoryRequest request) {
        return new ResponseEntity<>(analyzeService.analyzeByCategory(request), HttpStatus.OK);
    }

    /**
     * 
     * @body AnalyzeRequestByBudget 예산 분석 요청
     * @return AnalyzeResponseByBudget 예산 분석 응답
     */
    @PostMapping("/budget")
    public ResponseEntity<?> analyzeByBudget(@RequestBody AnalyzeRequestByBudget request) {
        return new ResponseEntity<>(analyzeService.analyzeByBudget(request), HttpStatus.OK);
    }

    /**
     * 
     * @body AnalyzeRequestByAsset 자산 분석 요청
     * @return AnalyzeResponseByAsset 자산 분석 응답
     */
    @PostMapping("/asset")
    public ResponseEntity<?> analyzeByAsset(@RequestBody AnalyzeRequestByAsset request) {
        return new ResponseEntity<>(analyzeService.analyzeByAsset(request), HttpStatus.OK);
    }
}
