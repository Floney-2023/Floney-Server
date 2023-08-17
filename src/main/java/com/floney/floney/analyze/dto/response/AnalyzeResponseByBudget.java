package com.floney.floney.analyze.dto.response;

import lombok.Getter;

@Getter
public class AnalyzeResponseByBudget {
    private final long leftMoney;
    private final long initBudget;

    private AnalyzeResponseByBudget(long totalIncomeMoney, long initBudget) {
        this.initBudget = initBudget;
        this.leftMoney = calculateLeftMoney(initBudget, totalIncomeMoney);
    }

    public static AnalyzeResponseByBudget of(Long totalIncome, Long initBudget) {
        return new AnalyzeResponseByBudget(totalIncome, initBudget);
    }

    private long calculateLeftMoney(long initBudget, long totalIncomeMoney) {
        return initBudget - totalIncomeMoney;
    }
}
