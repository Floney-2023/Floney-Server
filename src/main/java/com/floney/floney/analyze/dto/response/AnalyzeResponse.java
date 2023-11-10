package com.floney.floney.analyze.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@Getter
public class AnalyzeResponse {
    private static final int DIVIDE = 100;
    private final float total;
    private final float differance;
    private final List<AnalyzeResponseByCategory> analyzeResult;

    @Builder
    private AnalyzeResponse(float total, float differance, List<AnalyzeResponseByCategory> analyzeResult) {
        this.total = total;
        this.differance = differance;
        this.analyzeResult = analyzeResult;
    }

    public static AnalyzeResponse of(List<AnalyzeResponseByCategory> result, float totalMoney, float differance) {
        List<AnalyzeResponseByCategory> sortedResult = result.stream()
                .sorted(comparing(AnalyzeResponseByCategory::getMoney).reversed())
                .collect(toList());

        return AnalyzeResponse.builder()
                .analyzeResult(sortedResult)
                .total(totalMoney)
                .differance(differance)
                .build();
    }
}
