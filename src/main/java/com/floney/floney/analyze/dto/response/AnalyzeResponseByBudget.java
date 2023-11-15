package com.floney.floney.analyze.dto.response;

import lombok.Getter;

@Getter
public class AnalyzeResponseByBudget {
    private final double leftMoney;
    private final double initBudget;

    private AnalyzeResponseByBudget(double totalOutcomeMoney, double initBudget) {
        this.initBudget = initBudget;
        this.leftMoney = calculateLeftMoney(initBudget, totalOutcomeMoney);
    }

    public static AnalyzeResponseByBudget of(double totalOutcome, double initBudget) {
        return new AnalyzeResponseByBudget(totalOutcome, initBudget);
    }

    private double calculateLeftMoney(double initBudget, double totalOutcomeMoney) {
        return initBudget - totalOutcomeMoney;
    }
}
