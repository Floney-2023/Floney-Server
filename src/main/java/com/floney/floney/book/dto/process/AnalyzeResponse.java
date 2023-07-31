package com.floney.floney.book.dto.process;

import com.floney.floney.book.dto.response.AnalyzeByCategory;
import com.floney.floney.book.entity.BookAnalyze;
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
    private final List<AnalyzeByCategory> analyzeResult;

    @Builder
    private AnalyzeResponse(Long total, Long differance, List<AnalyzeByCategory> analyzeResult) {
        this.total = total;
        this.differance = differance;
        this.analyzeResult = analyzeResult;
    }


    public static AnalyzeResponse of(List<AnalyzeByCategory> result, BookAnalyze savedAnalyze, Long differance) {
        List<AnalyzeByCategory> sortedResult = result.stream()
            .sorted(comparing(AnalyzeByCategory::getMoney).reversed())
            .collect(toList());

        return AnalyzeResponse.builder()
            .analyzeResult(sortedResult)
            .total(savedAnalyze.getTotalMoney())
            .differance(differance)
            .build();
    }
}
