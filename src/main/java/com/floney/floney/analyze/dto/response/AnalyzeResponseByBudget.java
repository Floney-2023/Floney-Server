package com.floney.floney.analyze.dto.response;

import lombok.Getter;

@Getter
public class AnalyzeResponseByBudget {
    private final long leftMoney;
    private final long initBudget;

    private AnalyzeResponseByBudget(long totalOutcomeMoney, long initBudget) {
        this.initBudget = initBudget;
        this.leftMoney = calculateLeftMoney(initBudget, totalOutcomeMoney);
    }

    public static AnalyzeResponseByBudget of(Long totalOutcome, Long initBudget) {
        return new AnalyzeResponseByBudget(totalOutcome, initBudget);
    }

    private long calculateLeftMoney(long initBudget, long totalOutcomeMoney) {
        return initBudget - totalOutcomeMoney;
    }
}
