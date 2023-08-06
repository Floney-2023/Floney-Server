package com.floney.floney.analyze.controller;

import com.floney.floney.analyze.service.AnalyzeService;
import com.floney.floney.book.dto.request.AnalyzeByCategoryRequest;
import com.floney.floney.book.dto.request.AnalyzeRequestByAsset;
import com.floney.floney.book.dto.request.AnalyzeRequestByBudget;
import com.floney.floney.book.service.BookLineService;
import com.floney.floney.book.service.BookService;
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

    @GetMapping("/category")
    public ResponseEntity<?> analyzeByCategory(@RequestBody AnalyzeByCategoryRequest request) {
        return new ResponseEntity<>(analyzeService.analyzeByCategory(request), HttpStatus.OK);
    }

    @PostMapping("/budget")
    public ResponseEntity<?> analyzeByBudget(@RequestBody AnalyzeRequestByBudget request) {
        return new ResponseEntity<>(analyzeService.analyzeByBudget(request), HttpStatus.OK);
    }

    @PostMapping("/asset")
    public ResponseEntity<?> analyzeByAsset(@RequestBody AnalyzeRequestByAsset request) {
        return new ResponseEntity<>(analyzeService.analyzeByAsset(request), HttpStatus.OK);
    }
}
