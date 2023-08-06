package com.floney.floney.book.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class BudgetAnalyzeResponse {
    private final Long leftBudget;
    private final Long totalBudget;

    @QueryProjection
    public BudgetAnalyzeResponse(Long totalOutcome, Long totalBudget) {
        this.totalBudget = totalBudget;
        this.leftBudget = calculateLeftBudget(totalOutcome, totalBudget);
    }

    private Long calculateLeftBudget(Long totalOutcome, Long totalBudget) {
        return totalBudget - totalOutcome;
    }
}
