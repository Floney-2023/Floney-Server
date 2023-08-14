package com.floney.floney.analyze.dto.response;

import com.floney.floney.analyze.entity.BookAnalyze;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@Getter
public class AnalyzeResponse {
    private static final int DIVIDE = 100;
    private final Long total;
    private final Long differance;
    private final List<AnalyzeResponseByCategory> analyzeResult;

    @Builder
    private AnalyzeResponse(Long total, Long differance, List<AnalyzeResponseByCategory> analyzeResult) {
        this.total = total;
        this.differance = differance;
        this.analyzeResult = analyzeResult;
    }


    public static AnalyzeResponse of(List<AnalyzeResponseByCategory> result, BookAnalyze savedAnalyze, Long differance) {
        List<AnalyzeResponseByCategory> sortedResult = result.stream()
            .sorted(comparing(AnalyzeResponseByCategory::getMoney).reversed())
            .collect(toList());

        return AnalyzeResponse.builder()
            .analyzeResult(sortedResult)
            .total(savedAnalyze.getTotalMoney())
            .differance(differance)
            .build();
    }
}
